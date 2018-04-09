package ai.korio.services.modeler.bpmn

import ai.korio.services.codegen.CodeGenPlan
import org.joda.time.DateTime

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
        val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>
    }

    interface CamundaElementWithCodeGen: CamundaElement{
        override val definitionId: String
        override val elementId: String
        override val name: String
        override val type: String // base, extension, korio
        override val category: String // what tab it should go on
        override val help: String? // instructional text
        val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>
    }

    data class StringElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): BaseElement

    data class NumberElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: Number?
    ): BaseElement

    data class DateElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: DateTime?
    ): BaseElement

    data class UserElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): BaseElement

    data class UserGroupElement( // FIXME: does this hold a list of UserElements, or does UserElement contain groups?
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): BaseElement

}
