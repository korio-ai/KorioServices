package ai.korio.services.deployment

import ai.korio.services.CamundaEngine
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.repository.Deployment
import org.joda.time.DateTime

class DeploymentHandler {

    // TODO: determine if REST interface is sufficient for this https://docs.camunda.org/manual/latest/reference/rest/deployment/post-deployment/

    fun processDefinitionDeployment(processDeployment: DeploymentModels.NewProcessDeployment): Deployment {
        val repositoryService: RepositoryService = CamundaEngine().repositoryService!!
        val deployment: Deployment = repositoryService.createDeployment()
                .name("processDefDeployment" + DateTime.now())
                .source(processDeployment.source) //the application name, user name, etc.
                .addString(processDeployment.bpmnFileName, processDeployment.xml)
                // TODO: I think there is some sort of validation that can be done to make sure the bpmn is ok
                .deploy()
        return deployment
    }

    // TODO: more complex deployments with scripts, classes, etc...

}