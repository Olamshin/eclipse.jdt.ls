package org.eclipse.jdt.ls.core.internal.plantuml.logging;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Locale.ENGLISH;
import static java.util.ResourceBundle.getBundle;

/**
 * The resource messages used by the doclet.
 * <p>
 * The enumeration is chosen so we can easily test whether all messages
 * are contained by the resource bundle.
 *
 * @author Sjoerd Talsma
 */
public enum Message {
    DOCLET_VERSION,
    DOCLET_COPYRIGHT,
    DOCLET_UML_FOOTER,
    PLANTUML_COPYRIGHT,
    DEBUG_CONFIGURED_IMAGE_FORMATS,
    DEBUG_SKIPPING_FILE,
    DEBUG_REPLACING_BY,
    DEBUG_CANNOT_READ_ELEMENT_LIST,
    DEBUG_LIVE_PACKAGE_URL_NOT_FOUND,
    DEBUG_PACKAGE_VISITED_BUT_UNDOCUMENTED,
    INFO_GENERATING_FILE,
    INFO_GENERATING_SOURCE,
    INFO_ADD_DIAGRAM_TO_FILE,
    WARNING_UNRECOGNIZED_IMAGE_FORMAT,
    WARNING_CANNOT_READ_PACKAGE_LIST,
    WARNING_UNKNOWN_VISIBILITY,
    WARNING_PACKAGE_DEPENDENCY_CYCLES,
    ERROR_UNSUPPORTED_DELEGATE_DOCLET,
    ERROR_UNANTICIPATED_ERROR_GENERATING_UML,
    ERROR_UNANTICIPATED_ERROR_GENERATING_DIAGRAMS,
    ERROR_UNANTICIPATED_ERROR_POSTPROCESSING_HTML;

    private final String key = name().toLowerCase(ENGLISH).replace('_', '.');

    public String toString() {
        return toString(null);
    }

    public String toString(Locale locale) {
        final String bundleName = "Eclipse JDT";
        final ResourceBundle bundle = getBundle(bundleName, locale == null ? Locale.getDefault() : locale);
        return bundle.getString(key);
    }
}
