package ai.korio.services.codegen

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin("*")

class CodeGenController {

    data class CodeGenInitiator (  // TODO: replace or merge with CodeGenPlanModelInstance data class??
            val deep: Boolean,
            val codeArtifactType: String,
            val camDefinitionType: String,
            val camDefinitionId: String,
            val modelElementId: String
            )

    @PostMapping(path = arrayOf("code-gen"), produces = arrayOf("application/json"))
    fun generateCode(@RequestBody codeGenInitiator: CodeGenInitiator) {
        CodeGen().initialElementCodeGen(codeGenInitiator.deep, codeGenInitiator.codeArtifactType, codeGenInitiator.camDefinitionType, codeGenInitiator.camDefinitionId, codeGenInitiator.modelElementId)

    }


}