package ai.korio.services.modeler.bpmn

import org.camunda.bpm.model.bpmn.instance.UserTask

class TaskPropertiesHandler {

    fun getTaskElementAttributes(userTask: UserTask): MutableList<BpmnPropertiesHandler.ElementAttribute> {
        val attributes: MutableList<BpmnPropertiesHandler.ElementAttribute> = mutableListOf()
        userTask.camundaAssignee = "bob"
        // add name
        attributes.add(BpmnPropertiesHandler.ElementAttribute("name", "base", "general", "Name the element", "string", "name me", 0, userTask.name, 0))
        // add id
        attributes.add(BpmnPropertiesHandler.ElementAttribute("id", "base", "general", "Unique ID of the element", "string", "name me", 0, userTask.id, 0))
        userTask.elementType.attributes.map {
            System.out.println("task attribute: " + it.attributeName + "with value: " + it.getValue(userTask)?.toString() )
            attributes.add(BpmnPropertiesHandler.ElementAttribute(it.attributeName, "base", "all", "help text here", "string", "", 0, it.getValue(userTask)?.toString(), 0)) }
        userTask.extensionElements.elements.map {
            System.out.println("task extension element: " + it.elementType.typeName)
                // add extension element attributes
            it.elementType.attributes.map { extensionAttribute ->
                System.out.println("task attribute: " + extensionAttribute.attributeName + "; with value: " + extensionAttribute.getValue(userTask)?.toString())
                attributes.add(BpmnPropertiesHandler.ElementAttribute(extensionAttribute.attributeName, "extension", "all", "help text here", "string", "", 0, extensionAttribute.getValue(userTask)?.toString(), 0))
            }
        }

        return attributes
    }
}