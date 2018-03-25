package ai.korio.services.codegen

import freemarker.template.Configuration
import freemarker.template.Template
import java.io.OutputStreamWriter

class CodeGen {

    // Initialize config once in class per: https://kotlinlang.org/docs/reference/delegated-properties.html
    // TODO: go to above URL, see if this is truly loading once
    var cfg: Configuration by FreemarkerConfig("src/main/resources/templates/codegen")  // TODO: GitHub

/**
* For ALLOWED ELEMENTS ONLY, going element at a time, generates FULL code if deep is true.  NOTE: some elements, like DataSource,
 * may generate multiple files.
 * @param deep false if code gen is suppressed.  Usually still generates boilerplate code for copy/paste, but does NOT overwrite the code file
 * @param codeArtifactType specific type of code to create as a chunk; NOTE: elements may generate multiple code chunks
 * @param camDefinitionType whether Camunda BPMN, CMMN or DMN model
 * @param camDefinitionId the id of the Camunda definition
 * @param modelElementId the id of the actual element
* */
    fun initialElementCodeGen(deep: Boolean, codeArtifactType: String, camDefinitionType: String, camDefinitionId: String, modelElementId: String) {
        val cfg = CodeGen().cfg
        when (codeArtifactType){  // TODO: ideally this would be configurable as the only piece that needs hard-coding is the model definition
            "AvroSchema" -> CodeGenDataComponent().populateAvroSchemaModel(deep, camDefinitionType, camDefinitionId, modelElementId, cfg)
            "MobXStateTree" -> System.out.println("should generate mobx state tree")
            else -> {
                System.out.println("no code generator registered for ${codeArtifactType}. Initiated by code gen on ${modelElementId}")
            }
        }
    }

    /**
     * Each type of codeType Handler fills out its model and calls back into this function to
     * join model with template.
     * @param model kotlin data class returned by code type handler
     * @param template the Freemarker template template returned by code type handler
     * */
    fun generateCodeToComponent(model: Any, template: Template): String {

        val out = OutputStreamWriter(System.out)
        template.process(model, out)

        return out.toString() //FIXME: send to Mongo via CodeGenPlanModelInstance??

    }
    /**
     * Creates the code file.  May be a composition of multiple template outputs
     * */
    fun composeCodeComponentsToFile() {}
    /**
     * Executes a Pull Request on the provided source code repo
     * @param repository the repository to which the pull request is delivered.
     * */
    fun sendToPullRequest(repository: String) {}


}
