package ai.korio.services.modeler.bpmn

import ai.korio.services.codegen.CodeGenPlanConfig


/**
 * For setting up the Element Model
 * */
class ElementModel {

    interface BaseElement{
        val name: String
        val type: String // base, extension, korio
        val category: String // what tab it should go on
        val help: String? // instructional text

    }

    interface KorioElement: BaseElement{ // e.g. DataService
        val korioElementId: String
        override val name: String
        override val type: String // base, extension, korio
        override val category: String // what tab it should go on
        override val help: String? // instructional text
    }

    interface CamundaElement: BaseElement{
        val definitionId: String
        val elementId: String
        override val name: String
        override val type: String // base, extension, korio
        override val category: String // what tab it should go on
        override val help: String? // instructional text
    }

    interface KorioElementWithCodeGen: KorioElement{
        override val korioElementId: String
        override val name: String
        override val type: String // base, extension, korio
        override val category: String // what tab it should go on
        override val help: String? // instructional text
        val configuredCodeGenPlanModels: MutableList<CodeGenPlanConfig.CodeGenPlanModel>
    }

    interface CamundaElementWithCodeGen: CamundaElement{
        override val definitionId: String
        override val elementId: String
        override val name: String
        override val type: String // base, extension, korio
        override val category: String // what tab it should go on
        override val help: String? // instructional text
        val codeGenPlanModels: MutableList<CodeGenPlanConfig.CodeGenPlanModel>?
    }

    /**
     * Configuration for model elements. Fetches the form structure for each type of model element.
     * There must be one per type of element. Use ElementValue for each value for each
     * model element instance.
     * @param modelElementConfigs the child elements
     * @param definitionId the bpmn, dmn or cmmn definition id to which this element belongs
     * @param elementId the id of this particular element
     * */
    /* *
    TODO: and FIXME:
    A. TODO: FIXME: Do we need BOTH ModelElementConfig and a ModelElementValue to store the actual property values
    1. FIXME: In Kotlin, figure out how to do global variables, likely using "object"
    1a. FIXME: use the global object to init instantiateUserTaskModelElementConfig
    2. FIXME: Try to use a generic model element with generic sub elements for all model elements
    3. FIXME: Try to trap Camunda elements based on type setting and then feed them their values GENERICALLY in a looping mechanism
    */
    data class ModelElementConfig(  // meta model for elements
            override val definitionId: String,
            override val elementId: String,
            override val name: String,
            override val type: String, // base, extension, korio  FIXME: MUST NOT compromise Camunda variables as the engine needs them available and intact
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            // FIXME: if this is the meta-model, switch code gen to a meta model too.  Currently this points to the generated code for an element instance
            override val codeGenPlanModels: MutableList<CodeGenPlanConfig.CodeGenPlanModel>?,
            val modelElementConfigs: MutableList<ModelElementConfig>? // all the child element meta models
    ): CamundaElementWithCodeGen
}
