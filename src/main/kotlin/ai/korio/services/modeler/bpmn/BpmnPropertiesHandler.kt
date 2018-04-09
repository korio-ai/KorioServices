package ai.korio.services.modeler.bpmn

import ai.korio.services.KorioServicesApplication
import ai.korio.services.deployment.DeploymentHandler
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.instance.Process
import org.camunda.bpm.model.bpmn.instance.UserTask
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import org.camunda.bpm.model.xml.type.ModelElementType
import org.camunda.bpm.model.bpmn.instance.ExtensionElements
import org.camunda.bpm.model.bpmn.instance.Task


class BpmnPropertiesHandler {

    fun getModelDefinition(camDefinitionId: String): BpmnModelInstance = KorioServicesApplication().processEngine.repositoryService.getBpmnModelInstance(camDefinitionId)
    // FIXME: tasks created in the modeler are not getting a modelElementInstance from this. On first click, test for null an create/add the element to the model instance??
    fun getModelElementInstance(modelElementId: String, bpmnModelInstance: BpmnModelInstance): ModelElementInstance? = bpmnModelInstance.getModelElementById<ModelElementInstance>(modelElementId)
     //fun getModelElementType(modelElementId: String, bpmnModelInstance: BpmnModelInstance): ModelElementType = getModelElementInstance(modelElementId, bpmnModelInstance).elementType
    fun getModelElementTypeChildTypes(parentElementType: ModelElementType): MutableList<ModelElementType> = parentElementType.childElementTypes
    fun getModelElementAttributeValue(modelElementInstance: ModelElementInstance, attributeName: String): String = modelElementInstance.getAttributeValue(attributeName)


    data class BpmnModel(
            val modelId: String,
            val elements: MutableList<ElementAttribute>?
    )

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
     * On a "click" in the modeler, for each element type, gets the Model CamundaElement Attributes of the selected element, including
     * Camunda and Korio extension elements and their attributes
     * */
    fun getAndSetModelElementAttributes(camDefinitionId: String, modelElementId: String, modelElementType: String, modelElementName: String, isDirty: String): BpmnModel {
        val bpmnModelInstance = getModelDefinition(camDefinitionId)
        System.out.println("BPMN Model Instance before changes is based on Definition Id of: " + camDefinitionId)
        // While model seems to exist on the front-end, new elements don't exist on the backend. If the element instance doesn't exist, set it
        val modelElementInstance = getModelElementInstance(modelElementId, bpmnModelInstance) ?: setModelElementInstance(modelElementId, bpmnModelInstance, modelElementType)
        var elementAttributes:  MutableList<ElementAttribute> = mutableListOf()
        System.out.println("model element type is: " + modelElementInstance.elementType.typeName)
        when (modelElementInstance.elementType.typeName) {
            "userTask" -> {
                System.out.println("this is a user task")
                val userTask = modelElementInstance as UserTask
                // get the element's attributes, setting up any missing/custom/korio attributes if not already on the UserTask model class
                elementAttributes = UserTaskPropertiesHandler().getAndSetUserTaskElementsAndAttributes(userTask, modelElementName, isDirty) // fetch all the base and extended attributes and their values

            }
            "process" -> {  // TODO: move guts to ProcessPropertiesHandler class
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
        // need to deploy this updated process for it to be available to the front-end
        val newCamDefinitionId: String = DeploymentHandler().processDeploymentOnElementUpdate(camDefinitionId, bpmnModelInstance)
        val bpmnModel = BpmnModel(newCamDefinitionId,elementAttributes)
        // TODO: need this to RETURN both the newCamDefinitionId AND, I guess??, the elementAttributes
        //return elementAttributes
        return bpmnModel
    }

    /**
     * If Model CamundaElement Instance doesn't yet exist on the backend, create it AND set its TYPE
     * @param modelElementId the selected model element's id
     * @param bpmnModelInstance the instance of the model
     * @param modelElementType from the front-end, whether it is a bpmn:UserTask, etc.,
     * */
    fun setModelElementInstance(modelElementId: String, bpmnModelInstance: BpmnModelInstance, modelElementType: String): ModelElementInstance {
        val bpmnModelInstance = when (modelElementType) {
            "bpmn:UserTask" -> {bpmnModelInstance.newInstance(UserTask::class.java, modelElementId)}

            else -> {bpmnModelInstance.newInstance(Task::class.java, modelElementId)}
        }

       return bpmnModelInstance
    }



    /**
     * At model instance creation, creates Korio Extension Model CamundaElement Attributes
     * */
    // FIXME: left this shell, but it isn't called as attributes are added on first click, which could be a dangerous assumption.
    fun setKorioModelElementAttributes(modelInstance: BpmnModelInstance, seedTemplate: BpmnModelHandler.BpmnSeedTemplate)/*: List<ElementAttribute>*/ {
        //val bpmnModelInstance = getModelDefinition(camDefinitionId)
        //val modelElementInstance = getModelElementInstance(modelElementId, bpmnModelInstance)
        // gets the element type onto which the extensions will eventually be attached
        //val elementType: String = modelElementInstance.elementType.typeName
        //System.out.println("model element type is: " + elementType)


        seedTemplate.elements.forEach {

            when (it.type) {
                "userTask" -> {
                    // create a UserTask model instance
                    val userTask = modelInstance.newInstance(UserTask::class.java)
                    System.out.println("this is a user task")
                                       // see if there are extension elements for this model element
                    var extensionElements = userTask.getExtensionElements()
                    if (extensionElements == null) { // ...if no extension elements, add it to the overall model instance
                        extensionElements = modelInstance.newInstance(ExtensionElements::class.java)
                            // ...then attach it to userTask modelInstance
                        userTask.setExtensionElements(extensionElements)
                    }
                    // next, you have to get the existing extension elements
                    // val elements: MutableCollection<ModelElementInstance> = extensionElements.elements
                    // need elementAttributes to pass to the setKorioTaskElements function so it can add missing extension elements
                    //val steveExecutionListener = modelInstance.newInstance(CamundaExecutionListener::class.java)
                    //extensionElements.elements.add(steveExecutionListener)
                // FIXME: is any of this whole function even necessary?...
                    /*val camundaExecutionListener = userTask.modelInstance.newInstance(CamundaExecutionListener::class.java)
                    camundaExecutionListener.camundaClass = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"
                    userTask.builder().addExtensionElement(camundaExecutionListener)
                    val newExtensionElements = extensionElements.elementsQuery.filterByType(CamundaExecutionListener::class.java).singleResult()
                    System.out.println("newly added extension element: " + newExtensionElements.camundaFields.toString())
                    */

                    // UserTaskPropertiesHandler().setKorioTaskElementAttributes(userTask) // fetch all the base and extended attributes and their values
                }
                else -> {
                    System.out.println("this element type is not yet registered")
                }
            }

        }

        // now get and return all the elements to the front end
       // return getAndSetModelElementAttributes(camDefinitionId, modelElementId)
    }
    /**
     * Adds a single, non-standard attribute to an element
     * */
    fun addCustomModelElementAttribute() {

    }

}