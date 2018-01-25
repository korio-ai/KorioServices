package ai.korio.services

import org.camunda.bpm.engine.repository.CaseDefinition
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.engine.runtime.CaseExecution
import org.camunda.bpm.engine.runtime.CaseInstance
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.task.Task


class CommandHandler() {

    /**
     * Get commands for a Case Definition, usually to Start the case
     * */
    fun getCaseCommands(case: CaseDefinition): MutableList<Models.Command> {
        val caseCommands: MutableList<Models.Command> = mutableListOf()
        caseCommands.add(Models.Command("Start", "POST", "activate_case_from_id"))
        caseCommands.add(Models.Command("Assign", "POST", "bpmn-process-assign")) // FIXME
        caseCommands.add(Models.Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME
        return caseCommands
    }
    /**
     * Get commands for a Case Instance, usually to Work On the case
     * */
    fun getCaseInstanceCommands(instance: CaseInstance): MutableList<Models.Command> {
        val instanceCommands: MutableList<Models.Command> = mutableListOf()
        when (instance.isActive){
            true -> instanceCommands.add(Models.Command("Work On", "NAVIGATE", "bpmn-process-current-task")) // Get current task, usually from Status
            false -> instanceCommands.add(Models.Command("Start", "POST", "bpmn-process-start")) // Get current task, usually from Status

        }
        instanceCommands.add(Models.Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        instanceCommands.add(Models.Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return instanceCommands
    }
    /**
    * Get commands for a Case Execution
    * */
    fun getCaseExecutionCommands(execution: CaseExecution): MutableList<Models.Command> {
        val executionCommands: MutableList<Models.Command> = mutableListOf()
        when (execution.isActive){
            true -> executionCommands.add(Models.Command("Work On", "NAVIGATE", "bpmn-process-current-task")) // Get current task, usually from Status
            false -> executionCommands.add(Models.Command("Start", "POST", "bpmn-process-start")) // Get current task, usually from Status

        }
        executionCommands.add(Models.Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        executionCommands.add(Models.Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return executionCommands
    }


    /**
     * Get commands for a BPMN Process Instance
     * */

    fun getProcessInstanceCommands(instance: ProcessInstance, definition: ProcessDefinition): MutableList<Models.Command> {
        val executionCommands: MutableList<Models.Command> = mutableListOf()
        when (instance.isEnded){
            false -> executionCommands.add(Models.Command("Work On", "NAVIGATE", "bpmn-process-current-task")) // Get current task, usually from Status
            true -> executionCommands.add(Models.Command("Review", "NAVIGATE", "bpmn-process-review")) // Get current task, usually from Status

        }
        executionCommands.add(Models.Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        executionCommands.add(Models.Command("Discuss", "NAVIGATE", "bpmn-process-discuss")) // NOT task assign, which is different
        executionCommands.add(Models.Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return executionCommands
    }

    fun getTaskCommands(task: Task): MutableList<Models.Command> {
        val taskCommands: MutableList<Models.Command> = mutableListOf()
        when (task.isSuspended == false){  // FIXME: probably unnecessary when condition
            true -> taskCommands.add(Models.Command("Submit", "POST", "bpmn-submit-get-next-task")) // Get current task, usually from Status

        }
        taskCommands.add(Models.Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        taskCommands.add(Models.Command("Discuss", "NAVIGATE", "bpmn-process-discuss")) // NOT task assign, which is different
        taskCommands.add(Models.Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return taskCommands
    }

}