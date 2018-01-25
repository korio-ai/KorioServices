package ai.korio.services.cmmn


import ai.korio.services.Models
import ai.korio.services.Models.*
import ai.korio.services.bpmn.BpmnHandler
import org.camunda.bpm.engine.runtime.CaseInstance
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

@RestController
@CrossOrigin("*")
class CmmnController {


    /**
    * Gets all Cases that are available and can be started
    * */

    @GetMapping(path = arrayOf("cmmn/available_case_list"), produces = arrayOf("application/json"))
    fun getAvailableCaseList(): MutableList<MyCaseDefinition>  {
        return CmmnHandler().getAllavailableCases()
    }

    /**
    * Activate (start) an available case
    * */
    @PostMapping(path = arrayOf("cmmn/activate_case_from_id"), produces = arrayOf("application/json"))
    @ResponseStatus(HttpStatus.CREATED)
    fun startCaseFromId(@RequestBody newCase: NewCase): String {
        val caseInstanceId = CmmnHandler().activateCaseFromDefinitionId(newCase.caseId, newCase.caseName)
        return caseInstanceId
    }


    @GetMapping(path = arrayOf("cmmn/sse/available_case_list"), produces = arrayOf("text/event-stream"))
    fun getCaseDefinitionsReactive(): Flux<MyCaseDefinition> {
        return Flux
                .fromIterable(CmmnHandler().getAllavailableCases())
                .share() //TODO See Flux.share() to eliminate a request per client
                .subscribeOn(Schedulers.elastic()) //puts the subscription on a thread.  For a blog reactive example, see: http://musigma.org/java/2016/11/21/reactor.html

        //For Reactive SSE to Angular, see:https://github.com/kamilduda/spring-boot-2-reactive
        //Solved for Angular: https://blog.octo.com/en/angular-2-sse-and-changes-detection/
    }

    /**
    * Get all active cases
    * */
    @GetMapping(path = arrayOf("cmmn/active_case_list"), produces = arrayOf("application/json"))
    fun getActiveCaseList(): List<MyCaseInstance>  {
        return CmmnHandler().getAllActiveCases()
    }

    /*
* Get all plan items, including cases, that are active (have been started)
* FIXME: filter for cases only, not plan items
* */
    @GetMapping(path = arrayOf("cmmn/available_human_tasks"), produces = arrayOf("application/json"))
    fun availableHumanTasks(@RequestParam(value = "caseInstanceId") caseInstanceId: String): List<MyCaseExecution>  {
        return CmmnHandler().getEnabledHumanTaskExecutions(caseInstanceId)
    }

    /* Get all plan items, including cases, that are active (have been started)
    * FIXME: filter for cases only, not plan items
    * */
/*    @GetMapping(path = arrayOf("cmmn/available_bpmn_tasks_active"), produces = arrayOf("application/json"))
    fun activeBPMNTasks(@RequestParam(value = "caseInstanceId") caseInstanceId: String): List<MyCaseExecution>  {
        return CmmnHandler().getActiveBPMNExecutions(caseInstanceId)
    }*/
/**
 * calls BPMNHandler to grab actual activated BPMN processes
 * */
    @GetMapping(path = arrayOf("cmmn/available_bpmn_tasks_active"), produces = arrayOf("application/json"))
    fun activeBPMNProcesses(@RequestParam(value = "caseInstanceId") caseInstanceId: String): List<Models.MyCaseProcessInstanceAndDef>  {
        return BpmnHandler().getActiveBPMNProcessesById(caseInstanceId)
    }

/**
 * calls CmmnHandler to get Plan Items that are of type processTask.  These can be started and are then a proper
 * BPMN process instance (handled by BPMNHandler)
 * */
    @GetMapping(path = arrayOf("cmmn/available_bpmn_tasks_inactive"), produces = arrayOf("application/json"))
    fun inactiveBPMNTasks(@RequestParam(value = "caseInstanceId") caseInstanceId: String): List<Models.MyCaseExecution>  {
        return CmmnHandler().getInactiveBPMNExecutions(caseInstanceId)
    }
    /*
    * Get process description
    * */
    // FIXME: move to BpmnController... if two controller classes work!!
    // FIXME: shouldn't really be a POST in this case!!
    @PostMapping(path = arrayOf("bpmn/bpmn-process-description"), produces = arrayOf("application/json"))
    fun describeProcess(@RequestBody commandExecution: CommandExecution) {
        BpmnHandler().getBpmnProcessDescription(commandExecution.instanceId, commandExecution.activityType)

    }

    @PostMapping(path = arrayOf("bpmn/bpmn-process-start"), produces = arrayOf("application/json"))
    fun startBpmnProcess(@RequestBody commandExecution: CommandExecution) {
        BpmnHandler().startBpmnProcess(commandExecution.instanceId, commandExecution.activityType)

    }

    @GetMapping(path = arrayOf("bpmn/bpmn-process-current-task"), produces = arrayOf("application/json"))
    fun getCurrentBpmnTask(@RequestParam(value = "processInstanceId") processInstanceId: String): List<Models.MyTask> {
        return BpmnHandler().getCurrentTask(processInstanceId)
    }
/**
 * submits the captured task form data and then returns the next task
 * FIXME: handle last task
 * */
    @PostMapping(path = arrayOf("bpmn/bpmn-submit-get-next-task"), produces = arrayOf("application/json"))
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun finishCurrentGetNextBpmnTask(@RequestBody capturedData: CapturedData): MutableList<Models.MyTask> {
        val nextTasks: MutableList<Models.MyTask> = BpmnHandler().finishCurrentGetNextTask(capturedData)
        return nextTasks
    }


}
