package ai.korio.services.codegen

import ai.korio.services.modeler.bpmn.ElementModel
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.OutputStreamWriter


/**
 * Assumes that code is generated largely for the front end.  See readme.
 * */
class CodeGen {


    /**
     * The base interface for the code itself.
     * TODO: FIXME: this now just holds the code so PROBABLY need to remove KorioElement fields, which should be served
     * TODO: ...by the @param parentElementId
     * FIXME: on further review, some of this probably needs to be moved to CodeGenPlanConfig
     * */
    interface CamundaElementSourceCode: ElementModel.KorioElement {  // FIXME: doesn't need to be a KorioElement as this in now a value
        override val korioElementId: String
        val parentElementId: String // the model element that owns this
        override val name: String
        override val type: String // base, extension, korio
        override val category: String // what tab it should go on
        override val help: String? // instructional text
        val useGenericHandler: Boolean // if a generic handler exists, use it
        val genericHandler: CodeGenPlanConfig.CodeGenArtifacts // the generic handler
        val handlerType: String // Simple CamundaElementSourceCode, Function As Service
        val handlerLanguage: String
        val uriConfig: String // access to service, if not inline
        val code: String
        val memberOfFile: CodeGenPlanConfig.CodeGenFileComponents
    }

    /**
     * The source code saved at the component level... aggregated to the file level by CodeGenFileComponents
     * TODO: FIXME: this now just holds the code so PROBABLY need to remove KorioElement fields, which should be served
     * TODO: ...by the @param parentElementId
     * */
    data class CodeComponent(
            override val korioElementId: String,
            override val parentElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            override val useGenericHandler: Boolean, // if a generic handler exists, use it
            override val genericHandler: CodeGenPlanConfig.CodeGenArtifacts, // the generic handler
            override val handlerType: String, // Simple CamundaElementSourceCode, Function As Service
            override val handlerLanguage: String,
            override val uriConfig: String, // access to service, if not inline
            override val code: String,
            override val memberOfFile: CodeGenPlanConfig.CodeGenFileComponents
    ): CamundaElementSourceCode



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
     * Each type of codeType CamundaElementSourceCode fills out its model and calls back into this function to
     * join model with template.
     * @param model kotlin data class returned by code type handler
     * @param template the Freemarker template template returned by code type handler
     * */
    fun generateCodeToComponent(model: Any, template: Template): String {

        val out = OutputStreamWriter(System.out)
        template.process(model, out)

        return out.toString() //FIXME: send to Mongo via CodeGenPlanModel??

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
