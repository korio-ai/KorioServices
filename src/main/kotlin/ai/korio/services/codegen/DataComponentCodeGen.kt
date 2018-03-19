package ai.korio.services.codegen

import ai.korio.services.KorioServicesApplication
import freemarker.template.Configuration
import freemarker.template.Template

/**
 * The Data Component of the code gen system handles the schemas, data contracts, state management,
 * persistence, etc.
 * */
class DataComponentCodeGen(){


    /**
     * Model used to generate the data component of the code base, including schemas, state management, etc.
     * */
    data class DataCodeGenModel(
            val name: String
    )

    fun populateAvroSchemaModel(deep: Boolean, camDefinitionType: String, camDefinitionId: String, modelElement: String, cfg: Configuration): String {

        KorioServicesApplication().processEngine
        //TODO: look up Camunda element, populate from element extension properties
        val schema = DataCodeGenModel("steve schema")
        val template: Template = cfg.getTemplate("AvroSchema.ftl")
        return CodeGen().generateCodeToTemplate(schema, template)
    }



}
