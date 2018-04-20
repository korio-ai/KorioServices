package ai.korio.services.modeler.bpmn

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin("*")
class BpmnPropertiesController {


    @GetMapping(path = arrayOf("model/child-element"), produces = arrayOf("application/json"))
    fun getElementProperties(@RequestParam(value = "camDefinitionId") camDefinitionId: String,
                             @RequestParam(value = "modelElementId") modelElementId: String,
                            @RequestParam(value = "modelElementType") modelElementType: String,
                             @RequestParam(value = "modelElementName") modelElementName: String,
                             @RequestParam(value = "isDirty") isDirty: String) : BpmnPropertiesHandler.BpmnElementModel  {
        // FIXME: this is just a test.  Should only work for user task right now.
        return BpmnPropertiesHandler().getAndSetModelElementAttributes(camDefinitionId, modelElementId, modelElementType, modelElementName, isDirty )
    }

}