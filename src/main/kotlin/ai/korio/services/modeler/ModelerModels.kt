package ai.korio.services.modeler

class ModelerModels {

    data class CaseListItem(
            val name: String,
            val id: String, // case definition Id
            val documentation: String?
    )

    data class CaseHumanTaskListItem (
            val caseDefinitionId: String,
            val name: String
            // TODO: add things like candidate users, groups, etc, per HumanTask at: https://docs.camunda.org/javadoc/camunda-bpm-platform/7.8/org/camunda/bpm/model/cmmn/instance/HumanTask.html

    )

    data class CaseProcessTaskListItem (
            val caseDefinitionId: String,
            val processTaskId: String, // NOT the bpmn id.  This is the id given by the modeler to the Process Task.
            val name: String,
            val process: String?, // name of the BPMN process... not Id
            val bpmnDefinitionId: String?, // lookup of bpmn file based on name
            val documentation: String? //concatenated from a collection??, though only documentation on the "Definition" tab seems to show??
    )
}