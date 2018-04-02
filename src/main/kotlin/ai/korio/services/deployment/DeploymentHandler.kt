package ai.korio.services.deployment

import ai.korio.services.CamundaEngineConfig
import ai.korio.services.KorioServicesApplication
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
                .addModelInstance("new_process.bpmn", modelInstance)  // NOTE: need .bpmn here to tell the engine it is bpmn
                .deploy()
            val processDefinition: ProcessDefinition = processActivation(repositoryService, seedTemplate, deployment)
    // create a NEW BpmnSeedTemplate with the proper id and return it
        return BpmnModelHandler.BpmnSeedTemplate(seedTemplate.type, seedTemplate.parentId, processDefinition.id, seedTemplate.name, seedTemplate.elements )
    }

    // TODO: call this IF we want the new process deployment to be set to "active".  Currently called on new deployments automatically, which is probably not good
    private fun processActivation(repositoryService: RepositoryService, seedTemplate: BpmnModelHandler.BpmnSeedTemplate, deployment: Deployment): ProcessDefinition {
        // must activate deployed definition
        repositoryService.activateProcessDefinitionById(seedTemplate.id)
        // fetch the deployed, active definition so that we can send the id back to the front end so the XML can be loaded
        val processDefinition: ProcessDefinition = repositoryService.createProcessDefinitionQuery()
                .active()
                .deploymentId(deployment.id)// need this to get actual definition id from seedTemplate.id
                .singleResult()
        System.out.println("Deployed NEW process with full definition id: " + processDefinition.id)
        return processDefinition
    }

    // TODO:** FIXME: This is saving old XML over new model element attributes added from backend.  Rethink this!!
    // TODO: Consider "deactivating" processes under edit, since this is a save of a currently active process, it likely keeps the new version active, which is not ideal during long modeling processes
    fun processDeploymentOnSave(processDeployment: DeploymentModels.NewProcessDeployment): String { // TODO: clean up. Align with DeployOnNew
        val repositoryService: RepositoryService = CamundaEngineConfig().repositoryService!!
        val deployment: Deployment = repositoryService.createDeployment()
                .name("process_deployment_on_save" + DateTime.now())
                .source(processDeployment.source) //the application name, user name, etc.
                .addString(processDeployment.bpmnFileName, processDeployment.xml)
                // TODO: I think there is some sort of validation that can be done to make sure the bpmn is ok
                .deploy()
        return deployment.name
    }

    // TODO: this is very similar to processDeploymentOnSave, except this allows for backend updates that may be getting overwritten by...
    // TODO: ... processDeploymentOnSave.
    fun processDeploymentOnElementUpdate(oldProcessDefinitionId: String, bpmnModelInstance: BpmnModelInstance): String {
        val repositoryService = KorioServicesApplication().processEngine.repositoryService
        val deployment: Deployment = repositoryService.createDeployment()
                .name("process_deployment_on_elelement_update" + DateTime.now())
                .source("backend edit of element attributes")
                .addModelInstance("processDeploymentOnElementUpdate.bpmn", bpmnModelInstance)
                .deploy()
        System.out.println("just deployed on element update to deployment named: " + deployment.name)
        // FIXME: redundant with processActivation, clean up
        val processDefinition: ProcessDefinition = repositoryService.createProcessDefinitionQuery()
                .active()
                .deploymentId(deployment.id)// need this to get actual definition id from seedTemplate.id
                .singleResult()
        System.out.println("Updating from old process definition id of: " + oldProcessDefinitionId)
        System.out.println("Deployed UPDATED process with full definition id: " + processDefinition.id)

        // return new process definition Id so it can be sent to front end as new CamDefinitionId
        return processDefinition.id
    }

    // TODO: more complex deployments with scripts, classes, etc...

}