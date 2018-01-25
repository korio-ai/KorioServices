package ai.korio.services

import org.camunda.bpm.engine.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class CamundaEngine {  // TODO: Consider invoking these in the handler's constructor... though maybe no benefit.
    /**
     * Gets the Process Engine...usually the default
     */
    val processEngine: ProcessEngine? = ProcessEngines.getDefaultProcessEngine()
    /**
     * Gets the runtime service for BPMN processes
     */
    val runtimeService: RuntimeService? = processEngine?.runtimeService
    /**
     * Gets the case service - like the runtimeService, but for the plan items in individual cases (I think)
     */
    val caseService: CaseService? = processEngine?.caseService
    /**
     * Gets the repository service
     */
    val repositoryService: RepositoryService? = processEngine?.repositoryService

    val taskService: TaskService? = processEngine?.taskService

    val formService: FormService? = processEngine?.formService

}