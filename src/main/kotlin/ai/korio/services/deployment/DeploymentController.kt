package ai.korio.services.deployment

import org.camunda.bpm.engine.repository.Deployment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin("*")
class DeploymentController {


    /**
    * Activate (start) an available case
     * */
    @PostMapping(path = arrayOf("deploy/process"), produces = arrayOf("application/json"))
    fun newProcess(@RequestBody processDeployment: DeploymentModels.NewProcessDeployment): ResponseEntity<String> {
        val deploymentName = DeploymentHandler().processDeploymentOnSave(processDeployment)
        return ResponseEntity.ok().body(deploymentName)
    }
}