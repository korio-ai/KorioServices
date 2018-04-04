package ai.korio.services.modeler.bpmn

import ai.korio.services.codegen.CodeGenPlan

class ElementModel {

    interface Element{
        val name: String
        val type: String // base, extension, korio
        val category: String // what tab it should go on
        val help: String? // instructional text
    }


    data class StringElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): Element

    data class NumberElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): Element

    data class DateElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): Element

    data class UserElement(
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): Element

    data class UserGroupElement( // FIXME: does this hold a list of UserElements, or does UserElement contain groups?
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val value: String?
    ): Element

}
