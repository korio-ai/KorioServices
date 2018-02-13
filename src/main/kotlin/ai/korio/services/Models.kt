package ai.korio.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import org.camunda.bpm.engine.impl.core.variable.scope.VariableStore
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity
import org.camunda.bpm.engine.task.DelegationState
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

class Models {  //TODO: Camunda already has many of these models... why recreate them???

    data class Command(
            val commandName: String,
            val commandType: String, // NAVIGATE, GET or POST
            val commandExecutionURI: String  // TODO: explore Hateos, HAL and other hypermedia approaches.
    )

    data class CommandExecution (
            val instanceId: String,
            val activityType: String
    )
    /**
     * passes fields used to publish the object in a standard way. Publishes each field from
     * either WIP (Camunda) or a test or production content store.
     * For Publishing concept, see: https://github.com/snoop244/publish/issues/1
     * */
    data class DataPublish (

    val contentState: String, // PROD, TEST or EDIT, based on the repo the content was fetched from
    val contentURI: String // URI to fetch content from (necessary??) Parameters per: https://github.com/snoop244/publish/issues/2

    )

    /**
     * For form rendering...passes fields used by dynamic forms for data capture
     * Per Angular Formly: https://github.com/formly-js/ngx-formly
     * */
    data class FieldDataCapture (
            val key: String, // field name
            val type: String, // data type
            val templateOptions: TemplateOptions)

    data class TemplateOptions (
            val type: String?, // e.g. e-mail
            val label: String,
            val placeholder: String?,
            val required: Boolean
    )
    /**
     * Data coming back in
     * */
    data class CapturedData(
            val processInstanceId: String,
            val objectId: String, // whatever object is getting the data
            val capturedData: JsonNode // from jackson package to handle inbound JSON blob
            // FIXME: make sure capturedData is validated before submission as a straight submit is dangerous
    )


    data class Case(  // TODO: consider adopting Camunda's full Case model
            val caseId: String,
            val caseName: String,
            val caseKey: String,
            val caseEnabledCommands: List<Command>
    )

    data class MyCaseDefinition(
        val id: String, // 	The id of the case definition.
        val key: String?, // The key of the case definition, //  i.e., //  the id of the CMMN 2.0 XML case definition.
        val category: String?, // The category of the case definition.
        val name: String, // The name of the case definition.
        val version: Int?,	// The version of the case definition that the engine assigned to it.
        val resource: String?, //The file name of the case definition.
        val deploymentId: String?, // The deployment id of the case definition.
        val tenantId: String?, // The tenant id of the case definition.
        val historyTimeToLive: Int?, // History time to live value of the case definition. Is used within History cleanup.
        val enabledCommands: List<Command>
    )

    /*
    * Used as @ResponseBody when activating an enabled case
    * */
    data class NewCase(
            val caseId: String,
            val caseName: String  // TODO: can this even be used by a CaseExecution??
    )
    /**
     * CaseInstance holds the business key.  It has many CaseExecutions, which are Plan Items
     * */
    data class MyCaseInstance(
            val meta_CaseName: String, // the name of the case given by the user
            val id: String,	//The id of the case instance.
            val caseDefinitionId: String, // The id of the case definition that this case instance belongs to.
            val businessKey: String, // The business key of the case instance.
            val active:	Boolean, // A flag indicating whether the case instance is active or not.
            val completed: Boolean,	// A flag indicating whether the case instance is completed or not.
            val tenantId: String?, //The tenant id of the case instance.
            val enabledCommands: List<Command>
    )

    /**
    * Camunda CaseExecutionDto. Parent class of CaseInstanceDto
    * See: https://docs.camunda.org/javadoc/camunda-bpm-platform/7.8/org/camunda/bpm/engine/impl/cmmn/entity/runtime/CaseExecutionEntity.html
    * *///TODO: Camunda already has many of these models... why recreate them???
    data class MyCaseExecution(
            val active: Boolean,
            val activityDescription: String?,  // allow nulls??
            val activityId: String, // diff between this and id?
            val activityName: String?,
            val activityType: String?,
            val caseDefinitionId: String,
            val caseInstanceId: String,
            val disabled: Boolean,
            val enabled: Boolean,
            val id: String,
            val parentId: String?,
            val required: Boolean,
            val tenantId: String?,
            val enabledCommands: List<Command>
    )

    /**
     * BPMN Camumnda Process Instance PLUS Process Definition... not Plan Item
     * */
    data class MyCaseProcessInstanceAndDef (
            // From Camunda ProcessInstance per: https://docs.camunda.org/manual/7.8/reference/rest/process-instance/get/
            val id:	String,	// The id of the process instance.
            val definitionId: String, // The id of the process definition this instance belongs to.
            val businessKey: String?, //	The business key of the process instance.
            val caseInstanceId:	String, //The id of the case instance associated with the process instance.
            val ended: Boolean, // A flag indicating whether the process instance has ended or not. Deprecated: will always be false!
            val suspended: Boolean, // A flag indicating whether the process instance is suspended or not.
            val tenantId: String?, // The tenant id of the process instance.
            // From Camunda ProcessDefinition per: https://docs.camunda.org/manual/7.8/reference/rest/process-definition/get/
            val key: String?,	// The key of the process definition, i.e., the id of the BPMN 2.0 XML process definition.
            val category: String?,	//The category of the process definition.
            val description: String?,	// The description of the process definition.
            val name: String, //The name of the process definition.
            val version: Int?,	//The version of the process definition that the engine assigned to it.
            val resource: String?,	//The file name of the process definition.
            val deploymentId: String,	//The deployment id of the process definition.
            val diagram: String?,	//The file name of the process definition diagram, if it exists.
            val definitionSuspended: Boolean, // (suspended) A flag indicating whether the definition is suspended or not.
            val definitionTenantId: String?,	// (tenant) The tenant id of the process definition.
            val versionTag:	String?,	//The version tag of the process definition.
            val historyTimeToLive:	Int?, //History time to live value of the process definition. Is used within History cleanup.
            val enabledCommands: List<Command>
    )

    /**
    * A Task
    *
    * */
    data class MyTask (
            val id: String, //	The id of the task.
            val name: String, //	The tasks name.
            val assignee:  String?, //	The user assigned to this task.
            val created:  Date, //	The time the task was created. Format yyyy-MM-dd'T'HH:mm:ss.
            val due:  Date?, //	The due date for the task. Format yyyy-MM-dd'T'HH:mm:ss.
            val followUp:  Date?, //	The follow-up date for the task. Format yyyy-MM-dd'T'HH:mm:ss.
       // val delegationState:  String?, // FIXME: this should be an object of type DelegationState! // The delegation state of the task. Corresponds to the DelegationState enum in the engine. Possible values are RESOLVED and PENDING.
            val description:  String?, //	The task description.
            val executionId:  String, //	The id of the execution the task belongs to.
            val owner:  String?, //	The owner of the task.
            val parentTaskId:  String?, //	The id of the parent task, if this task is a subtask.
            val priority:	Int,	//The priority of the task.
            val processDefinitionId	:  String, //	The id of the process definition this task belongs to.
            val processInstanceId:  String, //	The id of the process instance this task belongs to.
            val caseExecutionId:  String?, // FIXME: why null?	The id of the case execution the task belongs to.
            val caseDefinitionId:  String?, //	The id of the case definition the task belongs to.
            val suspended: Boolean, // part of task??
            val caseInstanceId:  String, //	The id of the case instance the task belongs to.
            val taskDefinitionKey:  String, //	The task definition key.
      //  val formKey:  String?, // FIXME: RUNTIME ERROR: The form key is not initialized. You must call initializeFormKeys() on the task query before you can retrieve the form key.	If not null, the form key for the task.
            val tenantId:  String?, //	If not null, the tenantId for the task.
            val enabledCommands: List<Command>,
            val formKey: String?, // for custom layouts. if is not null AND if form markup exists, following fields will be ignored
            val dataCaptureFields: List<FieldDataCapture>? // only for user tasks
    )


    // TODO: create a CaseInstance??  In Camunda, just a sub-class of CaseExecution.  See: https://docs.camunda.org/javadoc/camunda-bpm-platform/7.8/org/camunda/bpm/engine/rest/dto/runtime/CaseInstanceDto.html

}