package ai.korio.services.modeler

import ai.korio.services.CamundaEngineConfig
import org.jvnet.hk2.annotations.Service
import java.io.InputStream
import org.camunda.bpm.model.cmmn.Cmmn
import org.camunda.bpm.model.cmmn.CmmnModelInstance
import org.camunda.bpm.model.cmmn.instance.Case
import org.camunda.bpm.model.cmmn.instance.ProcessTask


@Service
class CmmnModelHandler {

    fun getCaseModelInstanceFromStream(caseDefinitionId: String): CmmnModelInstance {

        val stream: InputStream = CamundaEngineConfig().repositoryService!!.getCaseModel(caseDefinitionId)
        return Cmmn.readModelFromStream(stream)
    }

    /**
     * Get process tasks (aka references to BPMN flows)
     * */
    fun getCaseModelsWithDetails(): MutableList<ModelerModels.CaseListItem> {
        val repositoryCases = CamundaEngineConfig().repositoryService!!.createCaseDefinitionQuery().latestVersion().list()
        val cases: MutableList<ModelerModels.CaseListItem> = mutableListOf()
        repositoryCases.forEach { repositoryCase ->
            val modelInstance: CmmnModelInstance = getCaseModelInstanceFromStream(repositoryCase.id)
            val caseType = modelInstance.model.getType(Case::class.java)
            val elementInstances = modelInstance.getModelElementsByType(caseType)
            elementInstances.forEach { elementInstance ->
                val case: Case = elementInstance as Case
                System.out.println("case name: " + case.name + " case id: " + case.id)
                var documentation: String = ""
                case.documentations.forEach {
                    documentation += it.textContent.toString() //concatenates documentation fields, if any.
                }
                val caseParsed: ModelerModels.CaseListItem = ModelerModels.CaseListItem(case.name, repositoryCase.id, documentation)// TODO: add new model type and create instance here
                cases.add(caseParsed)
            }
        }
        return cases
    }
    /**
     * Get process tasks (aka references to BPMN flows)
     * */
    fun getProcessTaskCaseModelElements(caseDefinitionId: String): MutableList<ModelerModels.CaseProcessTaskListItem> {
        val modelInstance: CmmnModelInstance = getCaseModelInstanceFromStream(caseDefinitionId) // FIXME: is this inefficient to call a bunch of times?
        val processTaskType = modelInstance.model.getType(ProcessTask::class.java)
        val processTasks: MutableList<ModelerModels.CaseProcessTaskListItem> = mutableListOf()
        val elementInstances = modelInstance.getModelElementsByType(processTaskType) // For ModelElementInstance, see: https://docs.camunda.org/javadoc/camunda-bpm-platform/7.8/org/camunda/bpm/model/xml/instance/ModelElementInstance.html
        elementInstances.forEach { elementInstance ->
            val processTask: ProcessTask = elementInstance as ProcessTask //cast a generic Model Element Instance to ProcessTask
            System.out.println("process task name is: " + processTask.name  + " id: " + processTask.id + " process: " + processTask.process )
            var bpmnDefinitionId: String = ""
            if (processTask.process != null) { // test if no bpmn reference... should not be permitted by editor, mind you
                // use key from ProcessTask and ".latestVersion" to get id of bpmn resource
                bpmnDefinitionId = CamundaEngineConfig().repositoryService!!.createProcessDefinitionQuery().processDefinitionKey(processTask.process).latestVersion().singleResult().id  //.processDefinitionName(processTask.process).singleResult().id
            } else {bpmnDefinitionId = "define process"}
            var documentation: String = ""
            processTask.documentations.forEach { document ->
                System.out.println("documentation... id: " + document.id + " content: " + document.textContent.toString())
                documentation += document.textContent.toString() //concatenates documentation fields, if any.
            }
            val processTaskParsed: ModelerModels.CaseProcessTaskListItem = ModelerModels.CaseProcessTaskListItem(caseDefinitionId, processTask.id, processTask.name, processTask.process, bpmnDefinitionId, documentation)
            processTasks.add(processTaskParsed)
        }
        return processTasks
    }


}