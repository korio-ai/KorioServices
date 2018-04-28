package ai.korio.services.codegen

import ai.korio.services.modeler.bpmn.ElementModel


/**
 * Allows for selection, execution and maintenance of a comprehensive Code Gen Plan.
 * Tests for completeness of the code generation.
 * Pulls in a configuration that provides all the code gen artifact types per element
 * Stores the code gen plan for each Service
 * Ensures that all code has been generated across all components
 *
 * TYPES of CodeGenPlanConfig:
 *  FRONTEND:
 *      FrontEnd Seed: Seed projects include a config file that getFrontEndCodeGenPlansFromSeed will call to understand available Plans
 *      Styles: [Case, Process, Task] for specifying CSS
 *      Barrel Files: [Platform, Case, Process, Task] holds all include/import references. See: https://medium.com/@adrianfaciu/barrel-files-to-use-or-not-to-use-75521cd18e65
 *      UserTask Data Capture Form: [Case, Process, Task, Submission/Channel/Field] form field layout + submit code (via state manager), (one per channel??), INCLUDES contextual data field publish
 *      Field-level Validation: [Field] NOTE: probably should NOT use CodeGenPlanConfig!!
 *      Field and Form-level Analytics: [Platform, Case, Process, Task, Submission/Channel/Field]
 *      Case State Management: [Case, Process] manage variable scope for overall Case/Service
 *      Process State Management: [Process] manage variable scope for Process
 *      Router: [Case, Process] showing the correct component for data capture, publish, etc.
 *      Object & Case View: [Platform, Case]: Working with Materialized Views in Data Service, creates object and case lists
 *      DataPublish: [Case, Process, Task] layout for rich data and content publishing
 *      Tests: ...including test of invariants (can models help with this??)
 *  BACKEND:
 *      Platform Configurator: [Platform, Case] sets up infra as code, picks Seeds
 *      BackEnd Seed: Seed projects include a config file that getBackEndCodeGenPlansFromSeed will call to understand available Plans
 *      API: [Case, Process, Task, Submission/Channel/Field] generates an API, e.g. GraphQL, REST, gRPC, Ws, SSE.  Assume  GraphQL for field-level api
 *      Generic Command Handler: [Case, Process, User Task, Submission/Channel/Field] A handler that generically passes data capture payload as a single Domain Event
 *      Custom Command Handler (side-effect): [System Task] creates one or more Domain Events for a SINGLE SIDE-EFFECT, code shell that allows data to be transformed or specially handled outside
 *          the edges of the Data Service. NOTE: that this should be largely MODELED and ensure the creation of a PURE FUNCTION with a single side-effect or
 *          a properly "contained" side-effect as per functional language practices.
 *      Custom Process Event Listener Handler: [Throw and Catch Events] ???
 *      Query: [System Task] Queries a materialized view, mutation-free (does this throw an event??... is it just a Custom Command Handler??)
 *      Tests: ...including modeling tests for invariants (for Handlers and front-end validation)...consider Given, When, Then (via Spock).
 *          Consider using/enforcing use of Camunda Throw Events and variable API... avoid excessive branching in model.
 *      DATA SERVICE:
 *          Persistence Strategy: [Platform, Case, Data Service] sets up DB with config, etc.
 *          Domain Data Service Instance: [Case] ideally one Data Service Instance per Case
 *          Governed Schema: [Platform, Case, Data Service] schema format to use, defaults to Avro
 *          Channel Submissions & Data Capture Coverage: [Case, Data Service, Submission/Channel/Field]holds inbound data until submission is ready, given multi-channel data capture and "self-healing"
 *              AI, seeks or reports on required/desired data.  Handles mis-ordered or duplicate submissions.
 *          Reactive Stream Publish/Subscribe: [Platform, Case, Data Service, Submission/Channel/Field]defines reactive framework (RxJava, Reactor, Akka), and pub/sub with Camunda, Domain Events, Data Flow Event Listeners
 *          Domain Event Persistence: [Platform, Case, Data Service]stores domain events
 *          Domain Event & Command Replay: [Platform, Case, Data Service] While Event replay is default to avoid side-effects, Command replay is an option, modeled on an
 *              element by element basis to allow side-effects, IF desired.
 *          Materialized View: [Platform, Case, Data Service] Query-side of CQRS/ES
 *      AI & MACHINE LEARNING
 *          AI/Machine Learning Processor: Listens on data stream
 *          AI/Machine Learning Model Deployer: Deploys and redeploys models to CMMN sentries, BPMN gateways, etc..
 *      ServiceTask 3rd Party REST calls:
 *
 * */
class CodeGenPlanConfig {

    /**
     * Set at Platform deployments and used by each element type
     * For the Platform, sets the types of CodeGenPlanConfig for each element type for the current deployment.
     * @param elementType Case, Process, Task, etc..
     * @param codeGenPlanModelsToConfigure the list of plans for the element type for this deployment. Set in the database.
     * */
    data class CodeGenPlanModelPlatformConfiguration(
            val elementType: String,
            val codeGenPlanModelsToConfigure: MutableList<CodeGenPlanModel>
    )
    /**
     * Stores the code gen plan model for an CamundaElement. See Types of CodeGenPlanConfig. Each element type needs multiple
     * codeGenPlans.
     * @param elementTypeLevel includes: Platform, Case, Process, Task, Data Service, Channel/Field
     * @param useUniversal if true, this scope should defer to the universal for this type and level
     * @param isUniversal true if THIS plan IS the universal plan
     * @param universalCodeGenModel the model to defer to if useUniversal is true
     * @param codeType see list, above
     * See comments at top of Class for codeGen types and levels
     * */
    data class CodeGenPlanModel(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text
            val elementTypeLevel: String,
            val codeType: String,
            val useUniversal: Boolean,
            val isUniversal: Boolean,
            val universalCodeGenModel: CodeGenPlanModel, // in situations were we can use a single, generic handler vs one per element
            val dirty: Boolean, // true if CodeGenArtifacts.dirty is true for any element??
            val codeGenArtifacts: CodeGenArtifacts?
    ): ElementModel.KorioElement
    /**
     * Holds the currently active code gen template AND active code artifacts
     * For the code gen plan model for the service, tracks which elements should NOT have DEEP code gen done for them,
     * sets "deep" flag to false on code gen function
     * */
    data class CodeGenArtifacts(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text,
            val deep: Boolean, // If false, suppresses overwriting code gen and offers shallow code snippets for use in custom code
            val dirty: Boolean,
            val activeCodeGenTemplate: CodeGenTemplate
           // val activeCode: CodeComponent // REMOVED as this class is just for config, not holding generated code
    ): ElementModel.KorioElement
    /**
     * The actual Freemarker template that handles code generation for the element in question
     * */
    data class CodeGenTemplate(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text,
            val uri: String
    ) : ElementModel.KorioElement
    /**
     * Defines the code file that will be generated from one or more code components. May be different from CodeGenTemplate
     * as the code files may be concatenated from multiple templates (TODO:...or not??)
     * */
    data class CodeGenFileComponents(
            override val korioElementId: String,
            override val name: String,
            override val type: String, // base, extension, korio
            override val category: String, // what tab it should go on
            override val help: String?, // instructional text,
            val fileName: String
          //  val codeComponents: MutableList<CodeComponent>? // REMOVED as this class is just for config, not holding generated code
    ) : ElementModel.KorioElement

    /**
     * Gets the code gen plan from all the available seed projects
     * @param gitSeedProjectURIs the list of available seeds, each of which have a code gen plan template in the repo.
     * */
    fun getFrontEndCodeGenPlansFromSeed(gitSeedProjectURIs: MutableList<String>): MutableList<String> {
       val availableSeeds = mutableListOf("React", "Angular", "Vue")
        return availableSeeds
    }
    fun getBackEndCodeGenPlansFromSeed(gitSeedProjectURIs: MutableList<String>): MutableList<String> {
        val availableSeeds = mutableListOf("SpringCloudStream", "SpringCloudDataFlow", "Vertx")
        return availableSeeds
    }
    /**
     * Based on available seeds, sets the overall code gen plan configuration for the given service/platform
     * Loads the code gen plan options into a database that will be available to all element types, based on
     * available seeds
     * @param serviceId the service that will be generated with the codeGenPlan
     * @param codeGenPlan the code gen plan pulled from the seed project
     * */
/*    fun setCodeGenPlan(serviceId: String, seed: String): CodeGenPlanModelElementConfiguration {
        val selectedCodeGenPlan = seed
        val codeGenPlanModelElementConfiguration: CodeGenPlanModelElementConfiguration =
        return codeGenPlanModelElementConfiguration
    }*/
    /**
     * Sets the code gen plan instance for the designated service id and saves to the store
     * TODO: manages suppressions
     * @param suppressCodeGenOnElementInstances user-suppressed code gen flag for named elements
     * */
/*    fun setCodeGenPlanInstance(serviceId: String, suppressCodeGenOnElementInstances: MutableList<CodeGenArtifacts>): CodeGenPlanModel {
        val artifacts: MutableList<CodeGenArtifacts>  = suppressCodeGenOnElementInstances
        val codeGenPlan = CodeGenPlanModel("React", false, artifacts)
        return codeGenPlan
    }*/
    /**
     * Gets the code gen plan instance for the designated service id
     * TODO: fetch from a store (MongoDB??)
     * */
/*    fun getCodeGenPlanInstance(serviceId: String): CodeGenPlanModel {
        val artifacts: MutableList<CodeGenArtifacts>  = mutableListOf() // TODO: this is placeholder code
        val codeGenPlan = CodeGenPlanModel("React", false, artifacts) // TODO: this is placeholder code
        return codeGenPlan
    }*/
    /**
     * Generate ALL code for the service, according to the plan instance (respecting suppressions but NOT dirty??)
     * */
    fun generateAllCodeForService(serviceId: String): String {

        return "success"
    }
    /**
     * Generate code for a single element of a service
     * */
    fun generateElementCodeForService(serviceId: String, elementId: String): String {

        return "success"
    }


}