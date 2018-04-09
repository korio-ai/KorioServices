package ai.korio.services.modeler.bpmn

import org.camunda.bpm.model.bpmn.instance.UserTask
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField



class UserTaskPropertiesHandler {
    /**
     *
     * */
    data class UserTaskModel(
            val modelId: String,
            val id: String,
            val name: ElementModel.StringElement,
            val assignee: ElementModel.UserElement?,
            val candidateUsers: MutableList<ElementModel.UserElement>?,
            val candidateGroups: MutableList<ElementModel.UserGroupElement>?,
            val dueDate: ElementModel.DateElement?,
            val followUpDate: ElementModel.DateElement?,
            val documentation: ElementModel.StringElement?,
            // Fields for standard, "wizard" based flow
            val standardComponentFlow: ElementChannelComponentFlowConfig.WizardTaskComponentFlow?,
            // Feilds aggregated for a default flow IF the user has permissions on each task
            val expertUserComponentFlow: ElementChannelComponentFlowConfig.FormTaskComponentFlow?, // wizard is default, this element aggregates to create longer forms, assuming the same doer
            // Remaining Component Flows are created as templated standard flows OR Camunda SubTasks, per: https://forum.camunda.org/t/creating-sub-tasks-for-a-user-task/137/2
            val chatComponentFlow: ElementChannelComponentFlowConfig.ChatComponentFlow?,
            val smsComponentFlow: ElementChannelComponentFlowConfig.SMSComponentFlow?,
            val emailComponentFlow: ElementChannelComponentFlowConfig.EmailComponentFlow?,
            val voiceComponentFlow: ElementChannelComponentFlowConfig.VoiceComponentFlow? // attempts to transcribe voice calls to form data e.g. using Twilio NLU

    )

    /**
     *
     * */
    fun getAndSetUserTaskElementsAndAttributes(userTask: UserTask, modelElementName: String, isDirty: String): MutableList<BpmnPropertiesHandler.ElementAttribute> {
        val attributes: MutableList<BpmnPropertiesHandler.ElementAttribute> = mutableListOf()
        if (userTask.extensionElements == null) { // if the extension elements aren't set up yet for UserTask model class, do so
                setKorioTaskElementAttributes(userTask) // add the extension elements
            }
        if (isDirty == "true"){ // if there has been a change in the front end, update
                // FIXME: should be called with a post or graphql mutation with DAO/model passed in
            userTask.camundaAssignee = "bob"
            // add name
            attributes.add(BpmnPropertiesHandler.ElementAttribute("name", "base", "general", "Name the element", "string", "name me", 0, modelElementName, 0))
            // add id
            attributes.add(BpmnPropertiesHandler.ElementAttribute("id", "base", "general", "Unique ID of the element", "string", "name me", 0, userTask.id, 0))
            updateUserTaskElementAttributes(userTask, attributes)
        }
        getUserTaskElementAttributes(userTask, attributes)
        return attributes
    }

    fun getUserTaskElementAttributes(userTask: UserTask, attributes: MutableList<BpmnPropertiesHandler.ElementAttribute>): MutableList<BpmnPropertiesHandler.ElementAttribute> {
        System.out.println("task name: " + userTask.name)
        System.out.println("task id: " + userTask.id)
        userTask.elementType.attributes.map {
            System.out.println("task attribute: " + it.attributeName + "with value: " + it.getValue(userTask)?.toString())
            attributes.add(BpmnPropertiesHandler.ElementAttribute(it.attributeName, "base", "all", "help text here", "string", "", 0, it.getValue(userTask)?.toString(), 0))
        }

        userTask.extensionElements.elements.map {
            System.out.println("task extension element: " + it.elementType.typeName)
            // add extension element attributes
            it.elementType.attributes.map { extensionAttribute ->
                System.out.println("task attribute: " + extensionAttribute.attributeName + "; with value: " + extensionAttribute.getValue(userTask)?.toString())
                attributes.add(BpmnPropertiesHandler.ElementAttribute(extensionAttribute.attributeName, "extension", "all", "help text here", "string", "", 0, extensionAttribute.getValue(userTask)?.toString(), 0))
            }
        }
        return attributes // FIXME: does this return fully updated attributes?
    }


    // FIXME: should be called with a post or graphql mutation with DAO/model passed in
    fun updateUserTaskElementAttributes(userTask: UserTask, attributes: MutableList<BpmnPropertiesHandler.ElementAttribute>) {
        // TODO: *** FIXME: move this to end of getAndSetModelElementAttributes as this is not task specific
        //val newCamDefinitionId: String = DeploymentHandler().processDeploymentOnElementUpdate(camDefinitionId, bpmnModelInstance)
        // TODO: ***need to add attributes to actual userTask element from attributes DTO

    }


    // FIXME: Side-Effect
    fun setKorioTaskElementAttributes(userTask: UserTask) {
        // TODO: test elementAttributes to ensure the element doesn't exist
        val camundaField = userTask.modelInstance.newInstance(CamundaField::class.java)
        camundaField.camundaName = "steve_field_name"
        userTask.builder().addExtensionElement(camundaField)

    }
}