package ai.korio.services.codegen

import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.File
import kotlin.reflect.KProperty

class FreemarkerConfig(templateURI: String) {

    val templateURI = templateURI

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Configuration {
        return configureFreemarker(templateURI)
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Configuration) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }

    fun configureFreemarker(templateURI: String): Configuration { // TODO: feed it GitHub URI eventually
        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.27) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        val cfg = Configuration(Configuration.VERSION_2_3_27)

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        cfg.setDirectoryForTemplateLoading(File(templateURI))  // TODO: apparently this does not have to be a file directory
        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.defaultEncoding = "UTF-8"

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.logTemplateExceptions = false

        // Wrap unchecked exceptions thrown during template processing into TemplateException-s.
        cfg.wrapUncheckedExceptions = true

        return cfg

    }
}