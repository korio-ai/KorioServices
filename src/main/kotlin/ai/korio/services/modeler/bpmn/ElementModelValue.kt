package ai.korio.services.modeler.bpmn

import org.joda.time.DateTime


/**
 * For storing and retrieving the actual value of an element for a specific definition
 * */
class ElementModelValue {

    interface BaseElementValue {
        val definitionId: String
        val elementId: String
    }

    data class StringElementValue(
            override val definitionId: String,
            override val elementId: String,
            val value: String?
    ): BaseElementValue

    data class NumberElementValue(
            override val definitionId: String,
            override val elementId: String,
            val value: Number?
    ): BaseElementValue

    data class DateElementValue(
            override val definitionId: String,
            override val elementId: String,
            val value: DateTime?
    ): BaseElementValue

    data class UserElementValue( // TODO: remove if no different than StringElementValue
            override val definitionId: String,
            override val elementId: String,
            val value: String?
    ): BaseElementValue

    data class UserGroupElement( // TODO: remove if no different than StringElementValue
            override val definitionId: String,
            override val elementId: String,
            val value: String?
    ): BaseElementValue

}