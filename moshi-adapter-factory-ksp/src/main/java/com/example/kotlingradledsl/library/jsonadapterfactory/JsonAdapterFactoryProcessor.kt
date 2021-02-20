package com.example.kotlingradledsl.library.jsonadapterfactory

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import java.io.OutputStream

class JsonAdapterFactoryProcessor : SymbolProcessor {
    private lateinit var codeGenerator: CodeGenerator
    private lateinit var logger: KSPLogger
    private lateinit var moduleName: String

    private val targets = mutableListOf<KSClassDeclaration>()

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator
        this.logger = logger
        this.moduleName = options["projectName"]!!
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols =
            resolver.getSymbolsWithAnnotation("com.squareup.moshi.JsonClass")

        symbols
            .filter { ksAnnotated ->
                ksAnnotated is KSClassDeclaration && ksAnnotated.validate()
            }
            .forEach { ksAnnotated ->
                ksAnnotated.accept(JsonClassAnnotationVisitor(targets), Unit)
            }

        return emptyList()
    }

    override fun finish() {

        val moduleName = moduleName.split("-")
            .fold(StringBuilder(), { builder, text ->
                builder.append(text.capitalize())
            })

        val shortestPkgName = targets.fold("") { shortenPkgName, ksClassDeclaration ->
            val currentPkgName = ksClassDeclaration.packageName.asString()
            if (shortenPkgName.isEmpty()) {
                return@fold currentPkgName
            }

            if (shortenPkgName.length < currentPkgName.length) {
                shortenPkgName
            } else {
                currentPkgName
            }
        }

        if (shortestPkgName.isEmpty()) return

        val importBuilder = StringBuilder()

        targets.forEach {
            importBuilder.append("|import ${it.qualifiedName!!.asString()}\n")
            importBuilder.append("|import ${it.qualifiedName!!.asString()}JsonAdapter\n")
        }

        val outputStream: OutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(
                true,
                *targets.map { it.containingFile!! }
                    .toTypedArray()
            ),
            packageName = shortestPkgName,
            fileName = "${moduleName}JsonAdapterFactory"
        )
        outputStream.appendText("""
            |package $shortestPkgName
            |
            |import com.squareup.moshi.JsonAdapter 
            |import com.squareup.moshi.Moshi
            |import com.squareup.moshi.Types
            |import java.lang.reflect.Type
            ${importBuilder}
            
            |class ${moduleName}JsonAdapterFactory: JsonAdapter.Factory {
            |
            |  override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
            |    if (annotations.isNotEmpty()) return null
            |    
            |    var clazz = Types.getRawType(type)
            |    
            |    if (clazz.isAnonymousClass && clazz.enclosingClass != null) {
            |      clazz = clazz.enclosingClass
            |    }           
            |
            |    return when (clazz) {
            ${
            targets.map { target -> "|      " + target.simpleName.asString() + "::class.java -> " + target.simpleName.asString() + "JsonAdapter(moshi)" }
                .fold(StringBuilder()) { builder, whenCase ->
                    builder.append(whenCase)
                        .appendLine()
                }
        }
            |      else -> null
            |    }
            |  }
            |}
        """.trimMargin())
        outputStream.close()
    }

    class JsonClassAnnotationVisitor(private val targets: MutableList<KSClassDeclaration>) :
        KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

            classDeclaration.annotations.firstOrNull { annotation ->
                annotation.shortName.asString() == "JsonClass"
            }?.arguments?.firstOrNull { argument ->
                argument.name?.asString() == "generateAdapter" && argument.value == true
            }
                ?.let {
                    targets.add(classDeclaration)
                }

        }
    }

}

private fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}