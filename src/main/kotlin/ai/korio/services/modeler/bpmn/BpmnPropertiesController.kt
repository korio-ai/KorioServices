package ai.korio.services.modeler.bpmn

import ai.korio.services.modeler.ModelerModels
import ai.korio.services.modeler.cmmn.CmmnModelHandler
import org.camunda.bpm.engine.impl.util.json.JSONObject
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import org.camunda.bpm.model.xml.type.ModelElementType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin("*")
class BpmnPropertiesController {


    @GetMapping(path = arrayOf("model/child-element"), produces = arrayOf("application/json"))
    fun getElementProperties(@RequestParam(value = "camDefinitionId") camDefinitionId: String,
                             @RequestParam(value = "modelElementId") modelElementId: String) : List<BpmnPropertiesHandler.ElementAttribute>  {
        return BpmnPropertiesHandler().getModelElementAttributes(camDefinitionId, modelElementId)
    }

}