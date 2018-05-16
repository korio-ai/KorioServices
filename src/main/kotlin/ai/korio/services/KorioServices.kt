package ai.korio.services

import ai.korio.services.cmmn.CmmnHandler
import ai.korio.services.codegen.CodeGen
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@SpringBootApplication
@EnableProcessApplication
@Configuration
class KorioServicesApplication {

    val processEngine: ProcessEngine by CamundaEngineConfig()  // calling a global instance of the process engine

    @Bean
    fun processCorsFilter(): FilterRegistrationBean<*> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)

        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.setOrder(0)
        return bean
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(KorioServicesApplication::class.java, *args)
    CmmnHandler().availableCaseDefinitions()  //TODO also activates cases and checks for active BPMN flows for now
 //   CmmnHandler().activateCaseFromDefinitionId("Claim_Case_1:1:36", "Work on your claim started December 21, 2017")
 //   CmmnHandler().activateCaseFromDefinitionId("SimpleCase:1:36", "stevekey2")
    CodeGen().initialElementCodeGen(true,"AvroSchema", "BPMN", "Claim_Start:1:38", "Task_0misudk")
}
