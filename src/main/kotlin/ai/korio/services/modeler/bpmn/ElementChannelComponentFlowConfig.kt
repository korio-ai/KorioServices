package ai.korio.services.modeler.bpmn

import ai.korio.services.codegen.CodeGenPlan

/**
 * Handles Multi Channel component/field flow adaptations around a master task concept.  Core flow is authored for Wizard-like execution
 * TODO: keep in mind that this is at the User Task Level whereas the concept needs to work at the Process level.
 * TODO: options to consider: do we use this to create alternate process definitions that can be edited, or do ...
 * TODO: ...we do it dynamically at runtime.  Probably the former so that micro-task instructional content ...
 * TODO: ...can be properly authored.
 * */
class ElementChannelComponentFlowConfig {

    /**
     * Rules of granularity concern themselves with what we call the "Wizard" flow.
     * There are two ideal rules at play to get to proper granularity here:
     *      1) nodes are Domain Event, or
     *      2) nodes are sized according to who can execute them.
     * Ideally these two rules lead to the same definition.
     *
     * In the case of 2), granularity complies with the standard modeling practice of user/candidate-based task granularity definition.
     * In other words, granularity is set based on the rule that each User Task can be executed by a named user.
     * Do not break up tasks if, in every instance, different users cannot be delegated the task. i.e. if one
     * user does all the tasks on a flow with no gateways and no delegation options, it should be one big task.  If a task
     * includes field groupings/sections, that can be completed by different users, break it up (and use FormTaskComponentFlow
     * to aggregate the form more efficient form completion for expert users)
     *
     * MasterComponentFlow is the "base task" and is part of the "base process".
     *
     * NOTE: If Triggers can be super-smart by knowing about their master, it may allow for a less "magic" approach vs
     * using Camunda SubTasks or AdHoc Tasks, which are dynamic and cannot be modeled. The benefit of modeling is that it allows
     * these alternate channel flows to be manually configured and designed.
     *
     * */


    /**
     * This is the standard UserTask.  It shows with all its active, parallel siblings on the same form based on formOrder
     * TODO: ***Try to decide which of these channel flows map to a domain event... probably Wizard???
     * */
    data class FormTaskComponentFlow(
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val parentTaskId: String?,
            val formOrder: Number, // Defaults to 1. Sets the order of this component if multiple UserTasks are active
            val formKey: String?, // korio extended
            val dataCaptureSubmission: ElementDataFieldModel.DataCaptureSubmissionModel?,
            val dataPublishFieldSet: ElementDataFieldModel.DataPublishFieldSet?,
            val commands: ai.korio.services.commands.CommandHandler.Command // ideally exposed as HATEOS resources

    ): ElementModel.CamundaElementWithCodeGen

    data class WizardTaskComponentFlow(
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val formKey: String?, // basic Camunda formKey
            val dataCaptureSubmission: ElementDataFieldModel.DataCaptureSubmissionModel?,
            val dataPublishFieldSet: ElementDataFieldModel.DataPublishFieldSet?,
            val commands: ai.korio.services.commands.CommandHandler.Command // ideally exposed as HATEOS resources

    ): ElementModel.CamundaElementWithCodeGen

    /**
     * Used to feed the configuration of a Camunda SubTask per: https://forum.camunda.org/t/creating-sub-tasks-for-a-user-task/137/2
     * */
    data class ChatComponentFlow( // This is probably a "Domain Event"
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val parentTaskId: String?,
            val formKey: String?, // korio extended
            val dataCaptureSubmission: ElementDataFieldModel.DataCaptureSubmissionModel?,
            val dataPublishFieldSet: ElementDataFieldModel.DataPublishFieldSet?,
            val commands: ai.korio.services.commands.CommandHandler.Command // ideally exposed as HATEOS resources
    ): ElementModel.CamundaElementWithCodeGen
    /**
     * Used to feed the configuration of a Camunda SubTask per: https://forum.camunda.org/t/creating-sub-tasks-for-a-user-task/137/2
     * */
    data class SMSComponentFlow(
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val parentTaskId: String?,
            val formKey: String?, // korio extended
            val dataCaptureSubmission: ElementDataFieldModel.DataCaptureSubmissionModel?,
            val dataPublishFieldSet: ElementDataFieldModel.DataPublishFieldSet?,
            val commands: ai.korio.services.commands.CommandHandler.Command // ideally exposed as HATEOS resources
    ): ElementModel.CamundaElementWithCodeGen
    /**
     * Used to feed the configuration of a Camunda SubTask per: https://forum.camunda.org/t/creating-sub-tasks-for-a-user-task/137/2
     * */
    data class EmailComponentFlow(
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val parentTaskId: String?,
            val formKey: String?, // korio extended
            val dataCaptureSubmission: ElementDataFieldModel.DataCaptureSubmissionModel?,
            val dataPublishFieldSet: ElementDataFieldModel.DataPublishFieldSet?,
            val commands: ai.korio.services.commands.CommandHandler.Command // ideally exposed as HATEOS resources
    ): ElementModel.CamundaElementWithCodeGen
    /**
     * Used to feed the configuration of a Camunda SubTask per: https://forum.camunda.org/t/creating-sub-tasks-for-a-user-task/137/2
     * */
    data class VoiceComponentFlow(
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val parentTaskId: String?,
            val formKey: String?, // korio extended
            val dataCaptureSubmission: ElementDataFieldModel.DataCaptureSubmissionModel?,
            val dataPublishFieldSet: ElementDataFieldModel.DataPublishFieldSet?,
            val commands: ai.korio.services.commands.CommandHandler.Command // ideally exposed as HATEOS resources
    ): ElementModel.CamundaElementWithCodeGen
}
