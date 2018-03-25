package ai.korio.services.codegen


/**
 * Allows for selection, execution and maintenance of a comprehensive Code Gen Plan.
 * Tests for completeness of the code generation.
 * Pulls in a configuration that provides all the code gen artifact types per element
 * Stores the code gen plan for each Service
 * Ensures that all code has been generated across all components
 *
 * */
class CodeGenPlan {
    /**
     * Stores the code gen plan model for a service
     * */
    data class CodeGenPlanModelInstance(
            val name: String,
            val dirty: Boolean, // true if CodeGenElementInstance.dirty is true for any element??
            val elementInstances: MutableList<CodeGenElementInstance>?
    )
    /**
     * For the code gen plan model for the service, tracks which elements should NOT have DEEP code gen done for them,
     * sets "deep" flag to false on code gen function
     * */
    data class CodeGenElementInstance(
            val elementId: String?,
            val deep: Boolean, // If false, suppresses overwriting code gen and offers shallow code snippets for use in custom code
            val dirty: Boolean
    )
    /**
     * Defines the code file that will be generated from one or more code components
     * */
    data class CodeGenFileComponents(
            val fileName: String,
            val codeComponents: MutableList<CodeComponent>?

    )
    /**
     * The source code saved at the component level... aggregated to the file level by CodeGenFileComponents
     * */
    data class CodeComponent(
            val componentName: String,
            val sourceCode: String
    )
    /**
     * Gets the code gen plan from all the available seed projects
     * @param gitSeedProjectURIs the list of available seeds, each of which have a code gen plan template in the repo.
     * */
    fun getAvailableCodeGenPlans(gitSeedProjectURIs: MutableList<String>): MutableList<String> {
       val availableSeeds = mutableListOf("React", "Angular", "Vue")
        return availableSeeds
    }
    /**
     * Based on available seeds, sets the selected seed's code gen plan for the given service
     * @param serviceId the service that will be generated with the codeGenPlan
     * @param codeGenPlan the code gen plan pulled from the seed project
     * */
    fun setCodeGenPlan(serviceId: String, seed: String, codeGenPlan: String): String {
        val selectedCodeGenPlan = seed
        return selectedCodeGenPlan
    }
    /**
     * Sets the code gen plan instance for the designated service id and saves to the store
     * TODO: manages suppressions
     * @param suppressCodeGenOnElementInstances user-suppressed code gen flag for named elements
     * */
    fun setCodeGenPlanInstance(serviceId: String, suppressCodeGenOnElementInstances: MutableList<CodeGenElementInstance>): CodeGenPlanModelInstance {
        val elementInstances: MutableList<CodeGenElementInstance>  = suppressCodeGenOnElementInstances
        val codeGenPlan = CodeGenPlanModelInstance("React", false, elementInstances)
        return codeGenPlan
    }
    /**
     * Gets the code gen plan instance for the designated service id
     * TODO: fetch from a store (MongoDB??)
     * */
    fun getCodeGenPlanInstance(serviceId: String): CodeGenPlanModelInstance {
        val elementInstances: MutableList<CodeGenElementInstance>  = mutableListOf() // TODO: this is placeholder code
        val codeGenPlan = CodeGenPlanModelInstance("React", false, elementInstances) // TODO: this is placeholder code
        return codeGenPlan
    }
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