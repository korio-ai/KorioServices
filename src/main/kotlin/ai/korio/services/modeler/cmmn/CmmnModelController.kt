package ai.korio.services.modeler.cmmn

import ai.korio.services.modeler.ModelerModels
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin("*")
class CmmnModelController{

    /**
     * Get all case definitions
     * */
    @GetMapping(path = arrayOf("model/case"), produces = arrayOf("application/json"))
    fun getHumanTaskListForCase(): MutableList<ModelerModels.CaseListItem> { //FIXME: make mutable list
        return CmmnModelHandler().getCaseModelsWithDetails()
    }

    /**
     * Get all human tasks for case definition id
     * */
/*    @GetMapping(path = arrayOf("model/human-task"), produces = arrayOf("application/json"))
    fun getHumanTaskListForCase(@RequestParam(value = "caseDefinitionId") caseDefinitionId: String): MutableList<HumanTask>  {
        return CmmnModelHandler().getHumanTaskCaseModelElements(caseDefinitionId)
    }*/
    /**
     * Get all process tasks for case definition id
     * */
    @GetMapping(path = arrayOf("model/process-task"), produces = arrayOf("application/json"))
    fun getProcessTaskListForCase(@RequestParam(value = "caseDefinitionId") caseDefinitionId: String): MutableList<ModelerModels.CaseProcessTaskListItem>  {
        return CmmnModelHandler().getProcessTaskCaseModelElements(caseDefinitionId)
    }

}