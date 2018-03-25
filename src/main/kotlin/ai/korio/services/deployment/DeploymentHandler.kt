package ai.korio.services.deployment

import ai.korio.services.CamundaEngineConfig
import ai.korio.services.modeler.bpmn.BpmnModelHandler
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.repository.Deployment
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.joda.time.DateTime

class DeploymentHandler {

        fun processDeploymentOnNew(seedTemplate: BpmnModelHandler.BpmnSeedTemplate, modelInstance: BpmnModelInstance): BpmnModelHandler.BpmnSeedTemplate {
        val repositoryService: RepositoryService = CamundaEngineConfig().repositoryService!!
        val deployment: Deployment = repositoryService.createDeployment()
                .name("new_process" + seedTemplate.name + " on_" + DateTime.now()) // NOTE: name of the deployment must have suffix of one of: bpmn20.xml, bpmn
                .source("korio IDE")
                .addModelInstance("new_process.bpmn", modelInstance)  // TODO: what is this string??
                .deploy()

        // must activate deployed definition
        repositoryService.activateProcessDefinitionById(seedTemplate.id)
        // fetch the deployed, active definition so that we can send the id back to the front end so the XML can be loaded
        val processDefinition: ProcessDefinition  = repositoryService.createProcessDefinitionQuery()
                .active()
                .deploymentId(deployment.id)// need this to get actual definition id from seedTemplate.id
                .singleResult()
        System.out.println("Deployed process with full definition id: " + processDefinition.id   )
        // create a NEW BpmnSeedTemplate with the proper id and return it
        return BpmnModelHandler.BpmnSeedTemplate(seedTemplate.type, seedTemplate.parentId, processDefinition.id, seedTemplate.name, seedTemplate.elements )
    }


    fun processDeploymenOnSave(processDeployment: DeploymentModels.NewProcessDeployment): Deployment { // TODO: clean up. Align with DeployOnNew
        val repositoryService: RepositoryService = CamundaEngineConfig().repositoryService!!
        val deployment: Deployment = repositoryService.createDeployment()
                .name("process_deployment_on_save" + DateTime.now())
                .source(processDeployment.source) //the application name, user name, etc.
                .addString(processDeployment.bpmnFileName, processDeployment.xml)
                // TODO: I think there is some sort of validation that can be done to make sure the bpmn is ok
                .deploy()
        return deployment
    }

    // TODO: more complex deployments with scripts, classes, etc...

}