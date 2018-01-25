package ai.korio.services.deployment

class DeploymentModels {

    /**
     * For new process definitions created from XML
     * */
    data class NewProcessDeployment(
            val source: String,
            val xml: String,
            val bpmnFileName: String
            // val category: String
    )

}