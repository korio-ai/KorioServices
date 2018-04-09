package ai.korio.services.modeler.bpmn

import ai.korio.services.codegen.CodeGenPlan
import org.camunda.bpm.model.bpmn.instance.ServiceTask
import org.joda.time.DateTime


/**
 * NOTE: THESE ARE USED IN THE MODELER.  MANY OF THESE CLASSES HAVE A RUN-TIME VARIANT THAT CARRIES A SMALLER
 * DATA SET.
 * */
class ElementDataFieldModel {

    data class ProcessAndTaskReference(
            val ProcessId: String?,
            val TaskId: String?
    )

    data class DataField(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String,
            override val help: String?,
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val dataType: String?,
            val stringValue: String?,
            val numberValue: Number?,
            val dateValue: DateTime,
            val validationRules: String? // front end validation NOT driven by codeGenPlan. Configuration based on data type, etc. see: https://validatejs.org/
    ): ElementModel.KorioElementWithCodeGen

    /**
     * Set within DataCaptureSubmissionModel, which is set within Channel Component Flow
     * Too granular for a Service Task
     * This class is used in the MODELER.  For run-time data-transfer, use the scaled-down version of this class
     * */
    data class SubmissionModel(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String,
            override val help: String?,
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val dataServiceRef: String?,  // FIXME: specified here or in DataCaptureSubmissionModel/PublishSource, or all 3?
            val masterSubmissionTrigger: DataCaptureSubmissionModel?, // the process and task that owns this field and sets submit timing/rules
            val dataFields: MutableList<DataField>? // the fields that make up the submission
    ): ElementModel.KorioElementWithCodeGen

    /**
     * AKA modeling a UserTask that represents a data capture "Domain Event"
     * Part of a User Task at the Channel Execution level.
     * This class is used in the MODELER.  For run-time data-transfer, use the scaled-down version of this class
     * Primarily set and managed from the Channel Component Flow objects (e.g. Wizard, Expert Form, SMS, etc.)
     * Provides a bridge/codegen to the API System Task within the data service that takes the data (e.g. the REST post)
     * It's counterpart is the PublishSource, which is tied to Materialized Views from the Data Service
     * */
    data class DataCaptureSubmissionModel(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val owningDataServiceRef: String?, // each trigger must be owned by its Data Service
            val owningDataSchemaRef: String?, // ID of schema from within Data Service
            val isMaster: Boolean?, // is this a master trigger, or a SubTask trigger?
            val masterSubmissionModel: DataCaptureSubmissionModel?, // if NOT a master, who is the master
            val parentTaskId: ProcessAndTaskReference?, // if a SubTask, need a reference to the master.
            val submission: MutableList<SubmissionModel> // FIXME: list, or just one?
    ): ElementModel.KorioElement

    /**
     * In the context of a User Task, published fields provide context to support data capture.  Rich publishing of content,
     * on the other hand, should be handled from a PublishSource Task in the DataServiceRef
     *
     * TODO: add edit permission facilities
     * */

    data class DataPublishFieldSet( // Fields published for giving context data capture tasks.  NOT the same as publishing a Materialized View
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String,
            override val help: String?,
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val dataFields: MutableList<DataField>?
    ): ElementModel.KorioElementWithCodeGen
    /**
     * Data assembled by the service for it's own processing needs.  Some variants of Materialized Views
     * will feed PublishSources, but this is a secondary usage
     * */
    data class MaterializedViewModel(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String,
            override val help: String?,
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val owningDataServiceRef: String?, // ID of data service
            val streamListenerServiceTasks: MutableList<ServiceTask>? // Service Tasks that listen on the stream for relevant data
    ): ElementModel.KorioElementWithCodeGen
    /**
    * This is NOT typically used to contextualize data capture.  This is usually a published representation of a
    * complex, materialized view that may even aggregate data from the stream.
    *
    * */
    data class PublishSourceModel(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String,
            override val help: String?,
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val owningDataServiceRef: String?, // ID of data service
            val materializedViewRef: String?, // ID of materialized view from within data service
            val parentTaskId: ProcessAndTaskReference?, // if a SubTask, need a reference to the master.
            val fieldsToPublish: MutableList<DataField>
    ): ElementModel.KorioElementWithCodeGen

    data class DataSchemaModel(  // TODO: decide if there should be an extended version for Aggregate Root
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String,
            override val help: String?,
            override val configuredCodeGenPlanModels: MutableList<CodeGenPlan.CodeGenPlanModel>,
            val owningDataServiceRef: String?, // ID of data service
            val isAggregateRoot: Boolean, // Is this the Aggregate root of the data service?
            val aggregateMemberChildrenRefs: MutableList<String?>, // if is Aggregate Root, what are the child scheams, if any
            val aggregateRootRef: String?,
            val dataCaptureSubmissionTriggers: MutableList<DataCaptureSubmissionModel> // responsible for submitting data
    ): ElementModel.KorioElementWithCodeGen
}