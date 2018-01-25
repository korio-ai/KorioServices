package ai.korio.services.bpmn

import ai.korio.services.CamundaEngine
import ai.korio.services.CommandHandler
import ai.korio.services.DataCaptureHandler
import ai.korio.services.Models
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Service

@Service
        class BpmnHandler {

    /**
     * start an enabled plan item (e.g. BPMN flow) by case execution id
     */
    fun manuallyStartCaseExecutionPlanItem(caseExecutionId: String) {
        val caseService = CamundaEngine().caseService!!  //TODO get one instance of case service on init and pass it around
        // TODO pass in a map from Avro schema: caseService.manuallyStartCaseExecution(caseExecutionId, Map)
        caseService.manuallyStartCaseExecution(caseExecutionId) // starts a process with its id
        System.out.printf("\nattempted to manually start case execution with ID: ${caseExecutionId}")
    }


    /**
     * List of running BPMN process instances for a given case instance id as BPMN instances not Case Plan Items!!
     */
    fun getActiveBPMNProcessesById(caseInstanceId: String):  MutableList<Models.MyCaseProcessInstanceAndDef> {
        val caseProcessInstanceAndDef: MutableList<Models.MyCaseProcessInstanceAndDef> = mutableListOf() // holds combo of instance and definition
        val BPMNProcessInstances: MutableList<ProcessInstance> = CamundaEngine().runtimeService!!
                .createProcessInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .active()
                .list()
        BPMNProcessInstances.forEach { //ProcessInstance only holds reference to a few things, including the Process Definition
            System.out.printf("\nActive BPMN process instance definition ID: ${it.processDefinitionId} and ${it.processInstanceId}")
            val processDefinition = CamundaEngine().repositoryService!!.getProcessDefinition(it.processDefinitionId) // query to get info
            val executionCommands: MutableList<Models.Command> = CommandHandler().getProcessInstanceCommands(it, processDefinition) // send both the instance and definition to command handler
            val bpmnCaseProcessInfo = Models.MyCaseProcessInstanceAndDef(
                    it.id,
                    it.processDefinitionId,
                    it.businessKey,
                    it.caseInstanceId,
                    it.isEnded,
                    it.isSuspended,
                    it.tenantId,
                    processDefinition.key,
                    processDefinition.category,
                    processDefinition.description,
                    processDefinition.name,
                    processDefinition.version,
                    processDefinition.resourceName, //FIXME: may not be correct
                    processDefinition.deploymentId,
                    processDefinition.diagramResourceName,
                    processDefinition.isSuspended,
                    "some tenant id",
                    processDefinition.versionTag,
                    processDefinition.historyTimeToLive,
                    executionCommands)
            caseProcessInstanceAndDef.add(bpmnCaseProcessInfo)
        }
        return caseProcessInstanceAndDef
    }


    fun getBpmnProcessDescription(instanceId: String, activityType: String) {

    }
/**
 * Starts the CMMN Plan Item
* */
    fun startBpmnProcess(instanceId: String, activityType: String) {
        System.out.println("\nstartBpmnProcess called with instanceId: ${instanceId} ")
        manuallyStartCaseExecutionPlanItem(instanceId)
        //TODO: load process variables...I assume they are already loaded??
    }
    /**
     * Queries the task interface, per: https://docs.camunda.org/javadoc/camunda-bpm-platform/7.8/org/camunda/bpm/engine/TaskService.html
     * Should return a single current task, but MAY return a list of current/parallel tasks for the current process instance id!!
     * */
    fun getCurrentTask(processInstanceId: String): MutableList<Models.MyTask> {
        System.out.println("\ngetCurrentTask called with instanceId: ${processInstanceId}")
        val currentTasks: MutableList<Models.MyTask> = mutableListOf()
        val tasks = CamundaEngine().taskService!!.createTaskQuery()
                .processInstanceId(processInstanceId)  // TODO: enable .taskAsignee(user)
                //.taskAssignee(user)
                //.active() // active is not a property of task directly, so it might be up the inheritance chain...
                .list() // should only return the current task.
        tasks.forEach {
            val taskCommands: MutableList<Models.Command> = CommandHandler().getTaskCommands(it) // send both the instance and definition to command handler
            val dataCaptureFields: MutableList<Models.FieldDataCapture> = DataCaptureHandler().getDataCaptureFields(it)
            val currentTaskItem = Models.MyTask(
                    it.id,
                    it.name,
                    it.assignee,
                    it.createTime,
                    it.dueDate,
                    it.followUpDate,
                 //   it.delegationState.toString(),  // FIXME: faking this as a string, it is a DelegationState! object
                    it.description,
                    it.executionId,
                    it.owner,
                    it.parentTaskId,
                    it.priority,
                    it.processDefinitionId,
                    it.processInstanceId,
                    it.caseExecutionId,
                    it.caseDefinitionId,
                    it.isSuspended,
                    it.caseInstanceId,
                    it.taskDefinitionKey,
                    //it.formKey,
                    it.tenantId,
                    taskCommands,
                    dataCaptureFields)
            currentTasks.add(currentTaskItem)

        }
        return currentTasks
    }


    /**
     * A list of current tasks that other(s) are accountable for.  MAY offer chance to claim claimable tasks as a
     * command
     *
     * */
    fun getUnassignedCurrentTask(processInstanceId: String): MutableList<Models.MyTask>{
        val unassignedCurrentTasks: MutableList<Models.MyTask> = mutableListOf();
        // TODO: send commands to claim task, view assignment, etc.
        return unassignedCurrentTasks
    }

/**
 * Submits task form variables as process instance variables then gets and returns the next task
 * FIXME: handle last task
 * */
    fun finishCurrentGetNextTask(capturedData: Models.CapturedData): MutableList<Models.MyTask>  {
        System.out.println("\nfinishCurrentGetNextTask called with object id: ${capturedData.objectId}")
        val variableMap: Map<String, Any> = DataCaptureHandler().parseFormPayload(capturedData) // need to pass the JsonNode through Jackson to parse to a variable map
        CamundaEngine().taskService!!.complete(capturedData.objectId, variableMap) //NB: creates variables on process instance, not task!!
        val nextTasks: MutableList<Models.MyTask> = getCurrentTask(capturedData.processInstanceId)
        return nextTasks //almost always a single task
    }
}