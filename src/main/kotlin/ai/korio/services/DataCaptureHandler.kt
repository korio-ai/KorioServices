package ai.korio.services

import org.camunda.bpm.engine.form.FormField
import org.camunda.bpm.engine.task.Task


/**
 * Supports dyanmic form capabilities for data capture.  Resulting data also needs to be parsed here from
 * a JsonNode as the payload is not of a defined type or model.  Any further handling of processing
 * should be handled by the CMMN, BPMN or DMN handler that owns submission of form data to the backend.
 * */
class DataCaptureHandler {
/**
* Sends the fields out to a dynamic form generator on the front-end, e.g. formly.
* */
    fun getDataCaptureFields(task: Task): MutableList<Models.FieldDataCapture> {
        val dataCaptureFields: MutableList<Models.FieldDataCapture> = mutableListOf()
            // TODO: fetch form data from FormService per: https://docs.camunda.org/javadoc/camunda-bpm-platform/7.8/org/camunda/bpm/engine/FormService.html
        // val variableMap: VariableMap = CamundaEngineConfig().formService!!.getTaskFormVariables(task.id) //gets variables and values as key-value pair
        val taskFormData = CamundaEngineConfig().formService!!.getTaskFormData(task.id)
        val formFields: List<FormField> =  taskFormData.formFields

        // TODO:  "value" seems to = default value, consider using it for placeholder??
        formFields.forEach {
            val templateOptions = Models.TemplateOptions(
                    "no type",
                    it.label,
                    null,
                    false
            )
            val field = Models.FieldDataCapture(
                    it.id,
                    it.typeName,
                    templateOptions
            )
            System.out.println( it.id + " is Id |   " + it.isBusinessKey + " is businessKey |  " + it.label + " is label |  " + it.type + " is type |  " + it.typeName + " is typename |  " + it.properties.toString() + " is properties to string |  ")
            dataCaptureFields.add(field)
        }
        return dataCaptureFields
    }
/**
 * Takes the inbound payload as a Jackson JsonNode from the form, via the handler, and returns a Camunda variable map
 *
 * */
    data class MyField(val key: String, val value: String)

    fun parseFormPayload(capturedData: Models.CapturedData) : Map<String, Any>
    {
     // per: https://stackoverflow.com/questions/39916520/mapping-a-dynamic-json-object-field-in-jackson
        val formData = capturedData.capturedData
        System.out.println("trying to parse capturedData json: ")
        System.out.println(formData)
        //val items: ObjectNode = formData as ObjectNode // cast??
        var variableMap: MutableMap<String, Any> = mutableMapOf()
        val elements = formData.fields() // need the fields extraction here or bad stuff happens
        elements.forEach {
            val field = MyField(it.key, it.value.toString()) // FIXME: need to make this serializable...trying with a data class...maybe try Spin??
            System.out.println("item by item: ")
            System.out.println(field.key)
            System.out.println(field.value)
            if (field.key.contains("meta_", false)) { // if it is a meta data field
                METADataFlow(capturedData.objectId, field)
            } else { // it is a normal process variable
                variableMap.put(field.key, field.value)
                }
            }

        System.out.println(variableMap)

        return variableMap
    }
    /**
     * Allows use of BPMN user tasks for updating Camunda meta data (e.g. Case Name)
     * */
    fun METADataFlow(itemId: String, field: MyField) {
        System.out.println("meta_ field encountered. Processing....")
        when (field.key) {
            "meta_CaseName" -> setCaseName(itemId, field.value)
                    else -> throw IllegalArgumentException("meta_ field was not recognized")
        }
    }

    fun setCaseName(itemId: String, name: String) {  //NOTE: setting a case variable for name as business key can't be changed??
        val engine = CamundaEngineConfig()
        val currentTask = engine.taskService!!.createTaskQuery().taskId(itemId).singleResult() // gets a single result from query instead of typical list
        val caseExecution = engine.caseService!!.createCaseExecutionQuery().caseInstanceId(currentTask.caseInstanceId).list() // executionId is null, must use instanceId
        caseExecution.forEach { // variables are only set on executions, not case instances?? There are multiple executions for the instance TODO: find out why
                val caseExecutionId = it.id
                engine.caseService!!.setVariable(caseExecutionId, "meta_CaseName", name)
            }

    }
}