package ai.korio.services.modeler.bpmn

import ai.korio.services.KorioServicesApplication
import org.camunda.bpm.engine.impl.util.json.JSONObject
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.instance.Process
import org.camunda.bpm.model.bpmn.instance.UserTask
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import org.camunda.bpm.model.xml.type.ModelElementType
import javax.xml.bind.Element
import ai.korio.services.modeler.bpmn.TaskPropertiesHandler

class BpmnPropertiesHandler {

    fun getModelDefinition(camDefinitionId: String): BpmnModelInstance = KorioServicesApplication().processEngine.repositoryService.getBpmnModelInstance(camDefinitionId)
    fun getModelElementInstance(modelElementId: String, bpmnModelInstance: BpmnModelInstance): ModelElementInstance = bpmnModelInstance.getModelElementById<ModelElementInstance>(modelElementId)
    fun getModelElementType(modelElementId: String, bpmnModelInstance: BpmnModelInstance): ModelElementType = getModelElementInstance(modelElementId, bpmnModelInstance).elementType
    fun getModelElementTypeChildTypes(parentElementType: ModelElementType): MutableList<ModelElementType> = parentElementType.childElementTypes
    fun getModelElementAttributeValue(modelElementInstance: ModelElementInstance, attributeName: String): String = modelElementInstance.getAttributeValue(attributeName)

    data class ElementAttribute(
            val name: String,
            val type: String, // base, extension, korio
            val category: String, // what tab it should go on
            val help: String?, // instructional text
            val dataType: String, // default to String or string?
            val defaultValueString: String?, // if the type is a String
            val defaultValueNumber: Number?, // if the type is a Number
            val valueString: String?, // the actual value
            val valueNumber: Number?
            )
    /**
     * For each element type, gets the Model Element Attributes of the selected element
     * */
    fun getModelElementAttributes(camDefinitionId: String, modelElementId: String): List<ElementAttribute> {
        val bpmnModelInstance = getModelDefinition(camDefinitionId)
        val modelElementInstance = getModelElementInstance(modelElementId, bpmnModelInstance)
        val elementType: String = modelElementInstance.elementType.typeName
        var elementAttributes:  MutableList<ElementAttribute> = mutableListOf()
        System.out.println("model element type is: " + elementType)
        when (modelElementInstance.elementType.typeName) {
            "userTask" -> {
                System.out.println("this is a user task")
                val userTask = modelElementInstance as UserTask
                elementAttributes = TaskPropertiesHandler().getTaskElementAttributes(userTask) // fetch all the base and extended attributes and their values
                }
            "process" -> {
                System.out.println("this is a process")
                val process = modelElementInstance as Process
                process.elementType.attributes.map {
                    System.out.println("process attribute: " + it.attributeName)
                    elementAttributes.add(ElementAttribute(it.attributeName, "base", "all", "help text here", "string", "", 0, "", 0)) }
                System.out.println("Is this process executable? " + process.isExecutable)
                }
            else -> {
                System.out.println("this element type is not yet registered")
                elementAttributes.add(ElementAttribute("not a registered element", "string", "base", "not a registered element", "string","not a registered element", 0, "not a registered element", 0))
                }
        }
        // modelElementInstance.setAttributeValue("name", "SteveSetName")
        return elementAttributes
    }

    fun setModelElementAttribute(camDefinitionId: String, modelElementId: String) {
        // TODO: on setting an attribute, determine whether the process needs to be DEPLOYED to fetch and show these
        // changes in the modeler
    }

}