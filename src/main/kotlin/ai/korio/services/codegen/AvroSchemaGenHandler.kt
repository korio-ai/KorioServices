package ai.korio.services.codegen

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import org.hibernate.validator.internal.xml.ContainerElementTypePath.root
import java.io.OutputStreamWriter
import kotlin.reflect.KProperty


class AvroSchemaGenHandler(){



    data class AvroSchemaModel(
            val name: String
    )

    fun populateAvroSchemaModel(camDefinitionType: String, camDefinitionId: String, modelElement: String, cfg: Configuration): String {
        //TODO: look up Camunda element, populate from element extension properties
        val schema = AvroSchemaModel("steve schema")
        val template: Template = cfg.getTemplate("AvroSchema.ftl")
        return CodeGen().generateCodeToTemplate(schema, template)
    }

/*    fun generateAvroSchema(camDefinitionType: String, camDefinitionId: String, modelElement: String): String {
        val cfg = CodeGen().cfg
        val schema = populateAvroSchemaModel(camDefinitionId, modelElement)
      //  val configuration: Configuration = configureFreemarker("src/main/resources/templates/codegen")
        val template: Template  = cfg.getTemplate("AvroSchema.ftl")
        val out = OutputStreamWriter(System.out)
        //template.process(root, out)
        template.process(schema, out)
    }*/

}
