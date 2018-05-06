package ai.korio.services

import freemarker.template.Configuration
import org.camunda.bpm.engine.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import kotlin.reflect.KProperty

@Service
class CamundaEngineConfig { // TODO: Consider invoking these in the handler's constructor... though maybe no benefit.

    /**
     * ?? Allows a application level version of the process engine to be called??
     * */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): ProcessEngine {
        return ProcessEngines.getDefaultProcessEngine()
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Configuration) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
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