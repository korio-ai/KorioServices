package ai.korio.services

import ai.korio.services.cmmn.CmmnHandler
import ai.korio.services.codegen.AvroSchemaGenHandler
import ai.korio.services.codegen.CodeGen
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableProcessApplication
class KorioServicesApplication

fun main(args: Array<String>) {
    SpringApplication.run(KorioServicesApplication::class.java, *args)
    CmmnHandler().availableCaseDefinitions()  //TODO also activates cases and checks for active BPMN flows for now
 //   CmmnHandler().activateCaseFromDefinitionId("Claim_Case_1:1:36", "Work on your claim started December 21, 2017")
 //   CmmnHandler().activateCaseFromDefinitionId("SimpleCase:1:36", "stevekey2")
    CodeGen().initiateElementCodeTypeGen("AvroSchema", "testDefinition", "testDefinitionId", "testModelElement")
}
