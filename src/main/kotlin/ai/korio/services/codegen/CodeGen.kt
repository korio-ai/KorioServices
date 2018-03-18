package ai.korio.services.codegen

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.io.OutputStreamWriter
import kotlin.reflect.KProperty

class CodeGen {

    // Initialize config once in class per: https://kotlinlang.org/docs/reference/delegated-properties.html
    // TODO: go to above URL, see if this is truly loading once
    var cfg: Configuration by FreemarkerConfig("src/main/resources/templates/codegen")  // TODO: GitHub

/**
* For ALLOWED ELEMENTS ONLY, going element at a time, generate code.  NOTE: some elements, like DataSource,
 * may generate multiple files
 * @param codeType specific type of code to create as a chunk; NOTE: elements may generate multiple code chunks
 * @param camDefinitionType whether Camunda Process, CMMN or DMN model
 * @param camDefinitionId the id of the Camunda definition
 * @param modelElement the id of the actual element
* */
    fun initiateElementCodeTypeGen(codeType: String, camDefinitionType: String, camDefinitionId: String, modelElement: String) {
    val cfg = CodeGen().cfg
    when (codeType){  // TODO: ideally this would be configurable as the only piece that needs hard-coding is the model definition
            "AvroSchema" -> AvroSchemaGenHandler().populateAvroSchemaModel(camDefinitionType, camDefinitionId, modelElement, cfg)
            "MobXStateTree" -> System.out.println("should generate mobx state tree")
            else -> { // Note the block
                System.out.println("no code generator registered for ${codeType}. Initiated by code gen on ${modelElement}")
            }
        }
    }

    fun generateCodeToTemplate(model: Any, template: Template): String {

        //  val configuration: Configuration = configureFreemarker("src/main/resources/templates/codegen")
        val out = OutputStreamWriter(System.out)
        //template.process(root, out)
        template.process(model, out)

        return out.toString()

    }



}

// Trying to initialize config once in above class per: https://kotlinlang.org/docs/reference/delegated-properties.html
class FreemarkerConfig(templateURI: String) {

    val templateURI = templateURI

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Configuration {
        return configureFreemarker(templateURI)
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Configuration) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }

    fun configureFreemarker(templateURI: String): Configuration { // TODO: feed it GitHub URI eventually
        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.27) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        val cfg = Configuration(Configuration.VERSION_2_3_27)

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        cfg.setDirectoryForTemplateLoading(File(templateURI))  // TODO: apparently this does not have to be a file directory
        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.defaultEncoding = "UTF-8"

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.logTemplateExceptions = false

        // Wrap unchecked exceptions thrown during template processing into TemplateException-s.
        cfg.wrapUncheckedExceptions = true

        return cfg

    }
}