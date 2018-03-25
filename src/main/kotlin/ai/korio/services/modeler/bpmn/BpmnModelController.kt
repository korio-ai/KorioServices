package ai.korio.services.modeler.bpmn

import org.camunda.bpm.engine.repository.Deployment
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class BpmnModelController {


    @PostMapping(path = arrayOf("model/bpmn"), produces = arrayOf("application/json"))
    fun newBpmnInstanceFromTemplate(@RequestBody seedTemplate: BpmnModelHandler.SeedData): BpmnModelHandler.BpmnSeedTemplate {
        return BpmnModelHandler().newBpmnInstanceFromTemplate(seedTemplate)
    }

}