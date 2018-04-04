package ai.korio.services.modeler.bpmn

import ai.korio.services.codegen.CodeGenPlan

/**
 * Handles Multi Channel adaptations of the core flow.  Core flow is authored for Wizard-like execution
 * TODO: keep in mind that this is at the User Task Level whereas the concept needs to work at the Process level.
 * TODO: options to consider: do we use this to create alternate process definitions that can be edited, or do ...
 * TODO: ...we do it dynamically at runtime.  Probably the former so that micro-task instructional content ...
 * TODO: ...can be properly authored.
 * */
class ElementChannelModel {

    /**
     * "Wizard" complies with the standard modeling practice of user/candidate-based task granularity definition.
     * In other words, granularity is set based on the rule that each User Task can be executed by a named user.
     * Do not break up tasks if, in every instance, different users cannot be delegated the task. i.e. if one
     * user does all the tasks on a flow with no gateways and no delegation options, it should be one task.  If a task
     * includes field groupins/sections, that can be completed by different users, break it up (and use ExpertUserFlow
     * to aggregate the form more efficient form completion for expert users)
     *
     * WizardFormFlow is the "base task" and is part of the "base process".
     * */
    data class WizardFormFlow(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val formKey: String?, // basic Camunda formKey
            val codeGenPlanModel: CodeGenPlan.CodeGenPlanModelInstance
    ): ElementModel.Element

    /**
     * Aggregates the base tasks of the base process based on a target user and their potential for ownership of
     * all tasks in a flow.
     * TODO: Should this be expressed at a task level AND process level? I'm assuming that this whole idea will
     * TODO: ...allow independent edits of an actual static process definition (or similar)... which implies
     * TODO: ...synchronization will be a challenge.
     * */
    data class ExpertUserFormFlow(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val derivedProcessDefId: String?,
            val derivedProcessTaskId: String?, // the task from the derived process that this is aggregated into
            val formKey: String?, // korio extended
            val codeGenPlanModel: CodeGenPlan.CodeGenPlanModelInstance
    ): ElementModel.Element

    data class ChatFormFlow(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val derivedProcessDefId: String?,
            val derivedProcessTaskIds: MutableList<String>?, // TODO: make a Map for ordering??
            val formKey: MutableList<String>?, // TODO: make a Map for ordering??
            val codeGenPlanModel: CodeGenPlan.CodeGenPlanModelInstance
    ): ElementModel.Element

    data class SMSFormFlow(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val derivedProcessDefId: String?,
            val derivedProcessTaskIds: MutableList<String>?, // TODO: make a Map for ordering??
            val formKey: MutableList<String>?, // TODO: make a Map for ordering??
            val codeGenPlanModel: CodeGenPlan.CodeGenPlanModelInstance
    ): ElementModel.Element

    data class EmailFormFlow(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val derivedProcessDefId: String?,
            val derivedProcessTaskIds: MutableList<String>?, // TODO: make a Map for ordering??
            val formKey: MutableList<String>?, // TODO: make a Map for ordering??
            val codeGenPlanModel: CodeGenPlan.CodeGenPlanModelInstance
    ): ElementModel.Element

    data class VoiceFormFlow(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val derivedProcessDefId: String?,
            val derivedProcessTaskIds: MutableList<String>?, // TODO: make a Map for ordering??
            val formKey: MutableList<String>?, // TODO: make a Map for ordering??
            val codeGenPlanModel: CodeGenPlan.CodeGenPlanModelInstance
    ): ElementModel.Element
}
