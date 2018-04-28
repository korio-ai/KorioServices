package ai.korio.services.modeler.bpmn

import ai.korio.services.codegen.CodeGenPlanConfig
import org.camunda.bpm.model.bpmn.instance.UserTask
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField


class UserTaskPropertiesHandler {

    /**
     *
     * */
/*    data class Model(
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val assignee: ElementModel.UserElement?,
            val candidateUsers: MutableList<ElementModel.UserElement>?,
            val candidateGroups: MutableList<ElementModel.UserGroupElement>?,
            val dueDate: ElementModel.DateElement?,
            val followUpDate: ElementModel.DateElement?,
            val documentation: ElementModel.StringElement?,
            val formKey: String?, // korio extended
            override val codeGenPlanModels: MutableList<CodeGenPlanConfig.CodeGenPlanModel>,
            val dataCaptureSubmission: ElementDataFieldModel.DataCaptureSubmissionModel?,
            val dataPublishFieldSet: ElementDataFieldModel.DataPublishFieldSet?,
            val commands: ai.korio.services.commands.CommandHandler.Command // ideally exposed as HATEOS resources

            ): ElementModel.CamundaElementWithCodeGen*/



    init {
        // FIXME: find a way to instantiate the modelElementConfig
    }
    /**
     * gets the model configuration from the database to create a UserTask Model Element
     * @param modelElementConfig a data structure that configures the UserTask Model Element
     * */
    fun instantiateUserTaskModelElementConfig(modelElementConfig: String): ElementModel.ModelElementConfig {
        // FIXME: TODO: feed from a modelElementConfig file or database
        val userTaskChildElementConfigs: MutableList<ElementModel.ModelElementConfig> = mutableListOf()
        val childelement1: ElementModel.ModelElementConfig = ElementModel.ModelElementConfig(
                "parent definition Id",
                "task child 1 element Id",
                "task child 1 element name",
                "task child 1 element type",
                "task child 1 element category",
                "task child 1 element help",
                null,
                null
        )
        val childelement2: ElementModel.ModelElementConfig = ElementModel.ModelElementConfig(
                "parent definition Id",
                "task child 2 element Id",
                "task child 2 element name",
                "task child 2 element type",
                "task child 2 element category",
                "task child 2 element help",
                null,
                null
        )
        userTaskChildElementConfigs.add(childelement1)
        userTaskChildElementConfigs.add(childelement2)
        val userTaskCodeGenPlans: MutableList<CodeGenPlanConfig.CodeGenPlanModel> = mutableListOf()
        val userTaskModelElement = ElementModel.ModelElementConfig(
                "parent definition Id",
                "task element Id",
                "task name",
                "task type",
                "task category",
                "task help",
                userTaskCodeGenPlans,
                userTaskChildElementConfigs
        )
        return userTaskModelElement
    }

    /**
     *
     * */
    fun getAndSetUserTaskElementsAndAttributes(userTask: UserTask, modelElementName: String, isDirty: String): MutableList<BpmnPropertiesHandler.ElementAttribute> {
        val userTaskModelElementConfigConfig: ElementModel.ModelElementConfig = instantiateUserTaskModelElementConfig("test")
        // TODO: use above variable to step through element config, rather than below
        // FIXME: elementConfig SHOULD NOT be set inside this function.  Try class init, autowired or similar
        val attributes: MutableList<BpmnPropertiesHandler.ElementAttribute> = mutableListOf()
        if (userTask.extensionElements == null) { // if the extension elements aren't set up yet for UserTask model class, do so
                setKorioTaskElementAttributes(userTask) // add the extension elements
            }
        if (isDirty == "true"){ // if there has been a change in the front end, update
                // FIXME: should be called with a post or graphql mutation with DAO/model passed in
            userTask.camundaAssignee = "bob"
            // add name
            attributes.add(BpmnPropertiesHandler.ElementAttribute("name", "base", "general", "Name the element", "string", modelElementName, 0))
            // add id
            attributes.add(BpmnPropertiesHandler.ElementAttribute("id", "base", "general", "Unique ID of the element", "string", userTask.id, 0))
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
            attributes.add(BpmnPropertiesHandler.ElementAttribute(it.attributeName, "base", "all", "help text here", "string", it.getValue(userTask)?.toString(), 0))
        }

        userTask.extensionElements.elements.map {
            System.out.println("task extension element: " + it.elementType.typeName)
            // add extension element attributes
            it.elementType.attributes.map { extensionAttribute ->
                System.out.println("task attribute: " + extensionAttribute.attributeName + "; with value: " + extensionAttribute.getValue(userTask)?.toString())
                attributes.add(BpmnPropertiesHandler.ElementAttribute(extensionAttribute.attributeName, "extension", "all", "help text here", "string", extensionAttribute.getValue(userTask)?.toString(), 0))
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