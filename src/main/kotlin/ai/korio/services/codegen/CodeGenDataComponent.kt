package ai.korio.services.codegen

import ai.korio.services.KorioServicesApplication
import freemarker.template.Configuration
import freemarker.template.Template
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.xml.instance.ModelElementInstance

/**
 * The Data Component of the code gen system handles the schemas, data contracts, state management,
 * persistence, etc.
 * */
class CodeGenDataComponent(){


    /**
     * Model used to generate the data component of the code base, including schemas, state management, etc.
     * */
    data class DataCodeGenModel(
            val name: String,
            val artifactVersion: String, //Not the same as file version as artifact may be just a component of a larger file
            val componentOfFile: String // The file that the generated code goes into.  It may be one of multiple code components of that file
    )

    fun populateAvroSchemaModel(deep: Boolean, camDefinitionType: String, camDefinitionId: String, modelElementId: String, cfg: Configuration): String {

        val bpmnModelInstance: BpmnModelInstance = KorioServicesApplication().processEngine.repositoryService.getBpmnModelInstance(camDefinitionId)
        //TODO: look up Camunda element, populate from element extension properties
        val modelElement: ModelElementInstance = bpmnModelInstance.getModelElementById<ModelElementInstance>(modelElementId)
        System.out.println("should print element name below this line:")
        System.out.println(modelElement.getAttributeValue("name"))
        val schema = DataCodeGenModel(modelElement.getAttributeValue("name"),"v.Test", "File1.avsc")
        val template: Template = cfg.getTemplate("AvroSchema.ftl")
        return CodeGen().generateCodeToComponent(schema, template)
    }



}
