package ai.korio.services.modeler.bpmn

import ai.korio.services.deployment.DeploymentHandler
import org.camunda.bpm.engine.repository.Deployment
import org.camunda.bpm.model.bpmn.Bpmn
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.instance.Definitions
import org.camunda.bpm.model.bpmn.instance.Process
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram
import org.camunda.bpm.model.xml.test.AbstractModelElementInstanceTest.*
import java.util.*
import java.util.stream.Stream


class BpmnModelHandler {

    data class BpmnSeedTemplate (
            val type: String?, // Usually CaseProcess or DataService
            val parentId: String?, // Usually a case id
            val id: String?,
            val name: String?,
            val elements: MutableList<BpmnElement> // initial elements
    )

    data class BpmnElement (  // FIXME: this isn't really used and probably can't be used with the builder
            val type: String?, // element type
            val id: String?,
            val name: String?
    )

    data class SeedData (
            val name: String,
            val parentId: String?,
            val type: String?
    )

    /**
     * Creates a new Bpmn flow, according to a seed template
     * @param seedTemplate the id of the parent case that will own the process
     * @param template allows a starter process to be used to initiate different types of processes
     * */
    fun newBpmnInstanceFromTemplate(seedData: SeedData): BpmnSeedTemplate { // FIXME: this is a heavy object to return.  Really not needed.
         val seedTemplate: BpmnSeedTemplate = getSeedTemplate(seedData)  // FIXME: this isn't really used and probably can't be used with the builder
         val modelInstance: BpmnModelInstance = when (seedData.type) {
           "CaseProcess" -> {
               Bpmn.createExecutableProcess(seedTemplate.id)
                    .name(seedTemplate.name)
                    .startEvent()
                    .userTask()
                    .done()

            }
            "DataService" -> {
                Bpmn.createExecutableProcess(seedTemplate.id)
                    .name(seedTemplate.name)
                    .startEvent()
                    .serviceTask("REST_API")
                    .serviceTask("GraphQL")
                    .serviceTask("BinaryAvro")
                    .done()

            }
            else -> {
                Bpmn.createExecutableProcess(seedTemplate.id)
                    .name("Generic Process")
                    .startEvent()
                    .done()
            }
        }
        return DeploymentHandler().processDeploymentOnNew(seedTemplate, modelInstance)

    }
    /**
     * TODO: Create templates here that can be used to seed new BPMN flows
     * */
    fun getSeedTemplate(seedData: SeedData): BpmnSeedTemplate {


         when (seedData.type) {
            "CaseProcess" -> {
                val elements: MutableList<BpmnElement> = mutableListOf() // FIXME: this isn't really used and probably can't be used with the builder
                val element1: BpmnElement = BpmnElement("startEvent", "startEvent_" + UUID.randomUUID(), seedData.name + "_forStartEvent")
                elements.add(element1)
                val caseSeed: BpmnSeedTemplate = BpmnSeedTemplate(
                        seedData.type,
                        seedData.parentId,
                        "CaseProcess_" + UUID.randomUUID(),
                        seedData.name,
                        elements
                )
                return caseSeed
            }
            "DataService" -> {
                val elements: MutableList<BpmnElement> = mutableListOf() // FIXME: this isn't really used and probably can't be used with the builder
                val element1: BpmnElement = BpmnElement("startEvent", "startEvent_" + UUID.randomUUID(), seedData.name + "_forStartEvent")
                elements.add(element1)
                val caseSeed: BpmnSeedTemplate = BpmnSeedTemplate(
                        seedData.type,
                        seedData.parentId,
                        "DataService_" + UUID.randomUUID(),
                        seedData.name,
                        elements
                )
                return caseSeed
            }
            else -> {
                val elements: MutableList<BpmnElement> = mutableListOf() // FIXME: this isn't really used and probably can't be used with the builder
                val element1: BpmnElement = BpmnElement("startEvent", "startEvent_" + UUID.randomUUID(), seedData.name + "_forStartEvent")
                elements.add(element1)
                val caseSeed: BpmnSeedTemplate = BpmnSeedTemplate(
                        seedData.type,
                        seedData.parentId,
                        "CaseProcess_" + UUID.randomUUID(),
                        seedData.name,
                        elements
                )
                return caseSeed
            }
        }
        }
    }