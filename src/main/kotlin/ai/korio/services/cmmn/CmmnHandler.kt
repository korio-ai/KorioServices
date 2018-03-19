package ai.korio.services.cmmn

import ai.korio.services.CamundaEngineConfig
import ai.korio.services.CommandHandler
import ai.korio.services.Models
import org.camunda.bpm.engine.repository.CaseDefinition
import org.camunda.bpm.engine.runtime.CaseExecution
import org.camunda.bpm.engine.runtime.CaseInstance
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.springframework.stereotype.Service


@Service
class CmmnHandler() {

    fun availableCaseDefinitions(): MutableList<CaseDefinition> {
        val availableCaseDefinitions: MutableList<CaseDefinition> = CamundaEngineConfig().repositoryService!!
                .createCaseDefinitionQuery()
                .list()
        availableCaseDefinitions.forEach {
            System.out.printf("\n Available Case Definition Name: ${it.name} and id: ${it.id}")
            //activateCaseFromDefinitionId(it.id, it.name) //TODO activate from "start new case" in UI
            //manuallyStartCaseExecution(it.id)
            //getActiveBPMNProcessesById(it.id)
        }
        return availableCaseDefinitions
    }

    //fetch and deserialize to list of strings for use in api calls
    fun getAllavailableCases(): MutableList<Models.MyCaseDefinition> {

        var availableCaseList: MutableList<Models.MyCaseDefinition> = mutableListOf()
        val cases: MutableList<CaseDefinition> = CmmnHandler().availableCaseDefinitions()
        cases.forEach {
            val caseCommands: MutableList<Models.Command>  = CommandHandler().getCaseCommands(it)
            val caseInfo = Models.MyCaseDefinition(
                    it.id,
                    it.key,
                    it.category,
                    it.name,
                    it.version,
                    it.resourceName,
                    it.deploymentId,
                    it.tenantId,
                    it.historyTimeToLive,
                    caseCommands) // TODO: are keys and businessKeys the same?
            availableCaseList.add(caseInfo)
        }
        return availableCaseList
    }

    fun activateCaseFromDefinitionId(caseId: String, caseName: String): String {  //TODO get case definition ID from user selection

        val caseService = CamundaEngineConfig().caseService!!
        val dt = DateTime()
        val fmt = DateTimeFormat.forPattern("MMMM dd, yyyy, HH:mm")
        val now = fmt.print(dt)
        val caseToday = caseName + " started on: " + now
        val nameMap: MutableMap<String, Any> = mutableMapOf() //to hold a case variable for meta_CaseName
        nameMap.put("meta_CaseName", "tempName") // Acts as case name until set in first Meta Process
        // TODO: I don't think caseId is the proper caseExecutionId that the next function needs
        val thisCase = caseService.createCaseInstanceById(caseId, caseToday, nameMap) //TODO not sure the difference between a CaseInstance and CaseExecution??  Execution does not seem to have a key
        System.out.printf("\n Case execution created with Activity Name: ${thisCase.activityName} with Case Instance ID: ${thisCase.caseInstanceId} with Business Key: ${thisCase.businessKey}")
        return thisCase.caseInstanceId
    }
    /**
    * Gets active case instances
    * */
    fun getAllActiveCases(): MutableList<Models.MyCaseInstance> {
        val activeCases: MutableList<Models.MyCaseInstance> = mutableListOf()
        val activeCaseService = CamundaEngineConfig().caseService!!
        var caseExecutionId: String = "" // needs to be initialized
        val cases: MutableList<CaseInstance> = activeCaseService.createCaseInstanceQuery()
                .active()
                .list()
        cases.forEach {
            // TODO: somehow get the case key, which, for some reason is not part of the CaseExecution object.
            val instanceCommands: MutableList<Models.Command> = CommandHandler().getCaseExecutionCommands(it)
            val caseExecution = activeCaseService.createCaseExecutionQuery().caseInstanceId(it.caseInstanceId).list() //need executionId to get vars
            caseExecution.forEach {
                caseExecutionId = it.id // there can only be one in this list ??
            }
            val META_CaseName: Any = activeCaseService.getVariable(caseExecutionId, "meta_CaseName" ) //FIXME: dangerous, needs try/catch ??
            val caseInfo = Models.MyCaseInstance(
                    META_CaseName.toString(), // should be a string already
                    it.id,
                    it.caseDefinitionId,
                    it.businessKey,
                    it.isActive,
                    it.isCompleted,
                    it.tenantId,
                    instanceCommands)
            activeCases.add(caseInfo)

        }
        return activeCases
    }

    /**
     * Gets plan items for a a specific case that are available, enabled but not yet active and that can be started
     * NB: a case "execution" is actually a planned item in a case instance.
     */
    fun getAllEnabledPlanItems(caseInstanceId: String): List<CaseExecution> = CamundaEngineConfig().caseService!!.createCaseExecutionQuery()
          //  .caseInstanceBusinessKey(caseInstanceBusinessKey)
            .caseInstanceId(caseInstanceId)
          //  .enabled()
            .list()
    /**
     * Gets all enabled process tasks for a given case instance key. NOTE: these are CMMN Plan Items
     */
    fun getEnabledHumanTaskExecutions(caseInstanceId: String): MutableList<Models.MyCaseExecution> { //TODO needs to return an array of the
     //   val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceBusinessKey)
        val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceId)
        var taskCommands: MutableList<Models.Command> = mutableListOf() // FIXME this is temprary mocking
        taskCommands.add(Models.Command("Mark Done","POST", "humantask-done")) // FIXME
        taskCommands.add(Models.Command("Assign", "POST","humantask-assign")) // FIXME
        var enabledHumanTaskExecutions: MutableList<Models.MyCaseExecution> = mutableListOf<Models.MyCaseExecution>()  //needed to initialialize the list...not sure if this works
        executions.forEach{
            if (it.activityType == "humanTask") {  // TODO: make activityType mandatory for MyCaseExecution ??
                val humanTaskInfo = Models.MyCaseExecution( // FIXME: this should be a Task Execution, not a Case Execution
                        it.isActive,
                        it.activityDescription,
                        it.activityId,
                        it.activityName,
                        it.activityType,
                        it.caseDefinitionId,
                        it.caseInstanceId,
                        it.isDisabled,
                        it.isEnabled,
                        it.id,
                        it.parentId,
                        it.isRequired,
                        it.tenantId,
                        taskCommands)
                enabledHumanTaskExecutions.add(humanTaskInfo)
                System.out.printf("\n  Human Task that is currently ENABLED: ${it.activityName}, activity type?: ${it.activityType},  Active?: ${it.isActive}")
            }
        }
        return enabledHumanTaskExecutions
    }

    /**
    * Gets all ENABLED BPMN executions, both ACTIVE and inACTIVE.  NOTE: these are CMMN Plan Items, not the underlying BPMN process!!
    * */
    fun getEnabledBPMNExecutions(caseInstanceId: String): MutableList<Models.MyCaseExecution> { //TODO needs to return an array of the
        //   val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceBusinessKey)
        val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceId)
        var taskCommands: MutableList<Models.Command> = mutableListOf() // FIXME this is temprary mocking
        taskCommands.add(Models.Command("Start", "POST", "bpmn-process-start")) // FIXME: switch to "Work-On"
        taskCommands.add(Models.Command("Next", "NAVIGATE","bpmn-process-current-task")) // Note: same effect as Start
        taskCommands.add(Models.Command("Assign", "POST","bpmn-process-assign"))
        taskCommands.add(Models.Command("More Info", "NAVIGATE","bpmn-process-description"))// FIXME: task vs process
        var enabledBPMNExecutions: MutableList<Models.MyCaseExecution> = mutableListOf<Models.MyCaseExecution>()  //needed to initialialize the list...not sure if this works
        executions.forEach {
            if (it.activityType == "processTask") {  // TODO: make activityType mandatory for MyCaseExecution ??
                val bpmnTaskInfo = Models.MyCaseExecution( // FIXME: this should be a Task Execution, not a Case Execution
                        it.isActive,
                        it.activityDescription,
                        it.activityId,
                        it.activityName,
                        it.activityType,
                        it.caseDefinitionId,
                        it.caseInstanceId,
                        it.isDisabled,
                        it.isEnabled,
                        it.id,
                        it.parentId,
                        it.isRequired,
                        it.tenantId,
                        taskCommands)
                enabledBPMNExecutions.add(bpmnTaskInfo)
                System.out.printf("\n Enabled process Task name: ${it.activityName}, activity type?: ${it.activityType},  Active?: ${it.isActive}")
            }
        }
        return enabledBPMNExecutions
    }

    /**
     * PROBABLY DON'T USE!! Try BPMNHandler().getActiveBPMNProcesses... instead. Gets all ENABLED BPMN executions that are currently ACTIVE.  NOTE: these are CMMN Plan Items, not the underlying BPMN process!!
     * */
    fun getActiveBPMNExecutions(caseInstanceId: String): MutableList<Models.MyCaseExecution> { //TODO needs to return an array of the
        //   val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceBusinessKey)
        val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceId)
        val enabledBPMNExecutions: MutableList<Models.MyCaseExecution> = mutableListOf()  //needed to initialialize the list...not sure if this works
        executions.forEach {
            if (it.activityType == "processTask" && it.isActive == true) {  // TODO: make activityType mandatory for MyCaseExecution ??
                val executionCommands: MutableList<Models.Command> = CommandHandler().getCaseExecutionCommands(it)
                val bpmnTaskInfo = Models.MyCaseExecution( // FIXME: this should be a Task Execution, not a Case Execution
                        it.isActive,
                        it.activityDescription,
                        it.activityId,
                        it.activityName,
                        it.activityType,
                        it.caseDefinitionId,
                        it.caseInstanceId,
                        it.isDisabled,
                        it.isEnabled,
                        it.id,
                        it.parentId,
                        it.isRequired,
                        it.tenantId,
                        executionCommands)
                enabledBPMNExecutions.add(bpmnTaskInfo)
                System.out.printf("\n Enabled process Task name: ${it.activityName}, activity type?: ${it.activityType},  Active?: ${it.isActive}")
            }
        }
        return enabledBPMNExecutions
    }

    /**
     * Gets all ENABLED BPMN executions that are currently INACTIVE.  NOTE: these are CMMN Plan Items, not the underlying BPMN process!!
     * */
    fun getInactiveBPMNExecutions(caseInstanceId: String): MutableList<Models.MyCaseExecution> { //TODO needs to return an array of the
        //   val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceBusinessKey)
        val executions: List<CaseExecution> = getAllEnabledPlanItems(caseInstanceId)
        var enabledBPMNExecutions: MutableList<Models.MyCaseExecution> = mutableListOf<Models.MyCaseExecution>()  //needed to initialialize the list...not sure if this works
        executions.forEach {
            if (it.activityType == "processTask" && it.isActive == false) {  // TODO: make activityType mandatory for MyCaseExecution ??
                val executionCommands: MutableList<Models.Command> = CommandHandler().getCaseExecutionCommands(it)
                val bpmnTaskInfo = Models.MyCaseExecution( // FIXME: this should be a Task Execution, not a Case Execution
                        it.isActive, // NOTE: boolean properties in Kotlin have "is..." added but the model needs to omit "is"!!
                        it.activityDescription,
                        it.activityId,
                        it.activityName,
                        it.activityType,
                        it.caseDefinitionId,
                        it.caseInstanceId,
                        it.isDisabled,
                        it.isEnabled,
                        it.id,
                        it.parentId,
                        it.isRequired,
                        it.tenantId,
                        executionCommands)
                enabledBPMNExecutions.add(bpmnTaskInfo)
                System.out.printf("\n Enabled process Task name: ${it.activityName}, activity type?: ${it.activityType},  Active?: ${it.isActive}")
            }
        }
        return enabledBPMNExecutions
    }


}