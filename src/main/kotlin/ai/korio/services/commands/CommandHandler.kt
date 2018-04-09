package ai.korio.services.commands

import org.camunda.bpm.engine.repository.CaseDefinition
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.engine.runtime.CaseExecution
import org.camunda.bpm.engine.runtime.CaseInstance
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.task.Task


class CommandHandler() {

    data class Command(
            val commandName: String,
            val commandType: String, // NAVIGATE, GET or POST
            val commandExecutionURI: String  // TODO: explore Hateos, HAL and other hypermedia approaches.
    ) {
        // TODO: Consider ENUMS and sealed classes in Kotlin, per: https://medium.com/grand-parade/6-magic-sugars-that-can-make-your-kotlin-codebase-happier-part-1-ceee3c2bc9d3
        data class CommandExecution (
                val instanceId: String,
                val activityType: String
        )
    }


    /**
     * Get commands for a Case Definition, usually to Start the case
     * */
    fun getCaseCommands(case: CaseDefinition): MutableList<Command> {
        val caseCommands: MutableList<Command> = mutableListOf()
        caseCommands.add(Command("Start", "POST", "activate_case_from_id"))
        caseCommands.add(Command("Assign", "POST", "bpmn-process-assign")) // FIXME
        caseCommands.add(Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME
        return caseCommands
    }
    /**
     * Get commands for a Case Instance, usually to Work On the case
     * */
    fun getCaseInstanceCommands(instance: CaseInstance): MutableList<Command> {
        val instanceCommands: MutableList<Command> = mutableListOf()
        when (instance.isActive){
            true -> instanceCommands.add(Command("Work On", "NAVIGATE", "bpmn-process-current-task")) // Get current task, usually from Status
            false -> instanceCommands.add(Command("Start", "POST", "bpmn-process-start")) // Get current task, usually from Status

        }
        instanceCommands.add(Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        instanceCommands.add(Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return instanceCommands
    }
    /**
    * Get commands for a Case Execution
    * */
    fun getCaseExecutionCommands(execution: CaseExecution): MutableList<Command> {
        val executionCommands: MutableList<Command> = mutableListOf()
        when (execution.isActive){
            true -> executionCommands.add(Command("Work On", "NAVIGATE", "bpmn-process-current-task")) // Get current task, usually from Status
            false -> executionCommands.add(Command("Start", "POST", "bpmn-process-start")) // Get current task, usually from Status

        }
        executionCommands.add(Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        executionCommands.add(Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return executionCommands
    }


    /**
     * Get commands for a BPMN Process Instance
     * */

    fun getProcessInstanceCommands(instance: ProcessInstance, definition: ProcessDefinition): MutableList<Command> {
        val executionCommands: MutableList<Command> = mutableListOf()
        when (instance.isEnded){
            false -> executionCommands.add(Command("Work On", "NAVIGATE", "bpmn-process-current-task")) // Get current task, usually from Status
            true -> executionCommands.add(Command("Review", "NAVIGATE", "bpmn-process-review")) // Get current task, usually from Status

        }
        executionCommands.add(Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        executionCommands.add(Command("Discuss", "NAVIGATE", "bpmn-process-discuss")) // NOT task assign, which is different
        executionCommands.add(Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return executionCommands
    }

    fun getTaskCommands(task: Task): MutableList<Command> {
        val taskCommands: MutableList<Command> = mutableListOf()
        when (task.isSuspended == false){  // FIXME: probably unnecessary when condition
            true -> taskCommands.add(Command("Submit", "POST", "bpmn-submit-get-next-task")) // Get current task, usually from Status

        }
        taskCommands.add(Command("Assign", "POST", "bpmn-process-assign")) // NOT task assign, which is different
        taskCommands.add(Command("Discuss", "NAVIGATE", "bpmn-process-discuss")) // NOT task assign, which is different
        taskCommands.add(Command("More Info", "NAVIGATE", "bpmn-process-description"))// FIXME: task vs process
        return taskCommands
    }


}