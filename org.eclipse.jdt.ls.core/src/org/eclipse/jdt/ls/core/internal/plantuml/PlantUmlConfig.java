package org.eclipse.jdt.ls.core.internal.plantuml;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.eclipse.jdt.ls.core.internal.plantuml.configuration.ImageConfig.Format.SVG;
import static org.eclipse.jdt.ls.core.internal.plantuml.configuration.Visibility.PACKAGE_PRIVATE;
import static org.eclipse.jdt.ls.core.internal.plantuml.configuration.Visibility.PROTECTED;
import static org.eclipse.jdt.ls.core.internal.plantuml.configuration.Visibility.PUBLIC;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jdt.ls.core.internal.plantuml.configuration.FieldConfig;
import org.eclipse.jdt.ls.core.internal.plantuml.configuration.ImageConfig;
import org.eclipse.jdt.ls.core.internal.plantuml.configuration.MethodConfig;
import org.eclipse.jdt.ls.core.internal.plantuml.configuration.TypeDisplay;
import org.eclipse.jdt.ls.core.internal.plantuml.configuration.Visibility;
import org.eclipse.jdt.ls.core.internal.plantuml.logging.Logger;
import org.eclipse.jdt.ls.core.internal.plantuml.logging.LoggerImpl;
import org.eclipse.jdt.ls.core.internal.plantuml.logging.Message;
import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.Indentation;


public class PlantUmlConfig implements Configuration {
    

        // private final UMLOptions options;
        // private volatile LocalizedReporter reporter;
    
        String plantumlServerUrl = null;
    
        /**
         * The name of the delegate doclet to use for the main documentation task.
         * <p>
         * Set to {@code StandardDoclet.class.getName()} by default.
         */
        String delegateDoclet = "jdk.javadoc.doclet.StandardDoclet";
    
        /**
         * Destination directory where documentation is generated.
         * <p>
         * Set by (Standard) doclet option {@code -d}, default is {@code ""} meaning the current directory.
         */
        String destDirName = "/Users/olamidekolawole/git/java/eclipse.jdt.ls/DELETE_ME";
    
        /**
         * Whether or not to render PlantUML {@code .puml} files.
         * <p>
         * Set by option {@code -createPumlFiles}, default is {@code false}.
         */
        boolean renderPumlFile = false;
    
        /**
         * Whether the doclet should run more quite (errors must still be displayed).
         * <p>
         * Set by (Standard) doclet option {@code -quiet}, default is {@code false}.
         */
        boolean quiet = false;
    
        /**
         * When not quiet, should the doclet be extra verbose?
         * <p>
         * Set by (our own) doclet option {@code -verbose}, default is {@code false}.
         */
        boolean verbose = false;
    
        /**
         * Option to provide explicit encoding for the written PlantUML files.
         * <p>
         * Otherwise, the {@link #htmlCharset()} is used.
         */
        String umlencoding;
    
        /**
         * Option for Standard doclet's HTML encoding.
         * <p>
         * This takes precedence over the {@code -encoding} setting which is about the source files.
         */
        String docencoding;
    
        /**
         * Option for Standard doclet's source encoding.
         * <p>
         * This is here because the Standard doclet uses this for HTML output if no {@code -docencoding} is specified.
         */
        String encoding;
    
        final ImageCfg images = new ImageCfg();
        final FieldCfg fieldConfig = new FieldCfg();
        final MethodCfg methodConfig = new MethodCfg();
        final Logger logger = new LoggerImpl();
    
        List<String> excludedReferences = new ArrayList<>(asList(
                "java.lang.Object", "java.lang.Enum", "java.lang.annotation.Annotation"));
    
        List<String> excludedPackageDependencies = new ArrayList<>(asList(
                "java", "javax"));
    
        boolean failOnCyclicPackageDependencies = false;
    
        // List<ExternalLink> externalLinks = new ArrayList<>();
    
        List<String> customPlantumlDirectives = new ArrayList<>();
    
        private Indentation indentation = Indentation.DEFAULT;
    
        public PlantUmlConfig() {
            // this.options = new UMLOptions(this);
            // this.reporter = new LocalizedReporter(this, null, null);
        }
    
        // public void init(Locale locale, Reporter reporter) {
            // this.reporter = new LocalizedReporter(this, reporter, locale);
        // }
    
        // public Set<Doclet.Option> mergeOptionsWith(Set<? extends Doclet.Option> standardOptions) {
        //     return options.mergeWith(standardOptions);
        // }
    
        @Override
        public Optional<String> plantumlServerUrl() {
            return Optional.ofNullable(plantumlServerUrl);
        }
    
        @Override
        public Optional<String> delegateDocletName() {
            return Optional.ofNullable(delegateDoclet).filter(name -> !"false".equalsIgnoreCase(name));
        }
    
        @Override
        public Logger logger() {
            // return reporter;
            return logger;
        }
    
        @Override
        public Indentation indentation() {
            return indentation;
        }
    
        @Override
        public String destinationDirectory() {
            return destDirName;
        }
    
        @Override
        public boolean renderPumlFile() {
            return renderPumlFile;
        }
    
        @Override
        public ImageConfig images() {
            return images;
        }
    
        @Override
        public FieldConfig fields() {
            return fieldConfig;
        }
    
        @Override
        public MethodConfig methods() {
            return methodConfig;
        }
    
        @Override
        public List<String> excludedTypeReferences() {
            return excludedReferences;
        }
    
        @Override
        public List<String> excludedPackageDependencies() {
            return excludedPackageDependencies;
        }
    
        @Override
        public boolean failOnCyclicPackageDependencies() {
            return failOnCyclicPackageDependencies;
        }
    
        // @Override
        // public Optional<URI> resolveExternalLinkToType(String packageName, String type) {
        //     return externalLinks.stream()
        //             .map(link -> link.resolveType(packageName, type))
        //             .filter(Optional::isPresent).map(Optional::get)
        //             .findFirst();
        // }
    
        @Override
        public List<String> customPlantumlDirectives() {
            return customPlantumlDirectives;
        }
    
        @Override
        public Charset umlCharset() {
            return umlencoding != null ? Charset.forName(umlencoding)
                    : htmlCharset();
        }
    
        @Override
        public Charset htmlCharset() {
            return docencoding != null ? Charset.forName(docencoding)
                    : encoding != null ? Charset.forName(encoding)
                    : Charset.defaultCharset();
        }
    
        private Set<Visibility> parseVisibility(String value) {
            if ("private".equals(value) || "all".equals(value)) return EnumSet.allOf(Visibility.class);
            else if ("package".equals(value)) return EnumSet.of(PACKAGE_PRIVATE, PROTECTED, PUBLIC);
            else if ("protected".equals(value)) return EnumSet.of(PUBLIC, PROTECTED);
            else if ("public".equals(value)) return EnumSet.of(PUBLIC);
    
            // reporter.warn(Message.WARNING_UNKNOWN_VISIBILITY, value);
            return parseVisibility("protected"); // The default for javadoc
        }
    
        void showMembers(String value) {
            Set<Visibility> visibility = parseVisibility(value);
            fieldConfig.visibilities = visibility;
            methodConfig.visibilities = visibility;
        }
    
        final class ImageCfg implements ImageConfig {
            String directory = null;
            Collection<Format> formats = null;
    
            /**
             * Directory where UML images are generated.
             * <p>
             * Set by doclet option {@code -umlImageDirectory}, default is {@code empty} meaning relative to the generated
             * documentation itself.
             */
            @Override
            public Optional<String> directory() {
                return Optional.ofNullable(directory);
            }
    
            void addImageFormat(String imageFormat) {
                if (imageFormat != null) {
                    if (formats == null) formats = new LinkedHashSet<>();
                    Stream.of(imageFormat.split("[,;]"))
                            .map(String::trim).map(String::toUpperCase)
                            .map(s -> s.replaceFirst("^\\.", ""))
                            .filter(s -> !s.isEmpty() && !"NONE".equals(s))
                            .map(this::parseFormat).filter(Objects::nonNull)
                            .forEach(formats::add);
                }
            }
    
            private Format parseFormat(String format) {
                try {
                    return Format.valueOf(format);
                } catch (IllegalArgumentException unrecognizedFormat) {
                    logger().warn(Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT, format);
                    return null;
                }
            }
    
            @Override
            public Collection<Format> formats() {
                return Optional.ofNullable(formats).orElseGet(() -> singleton(SVG));
            }
        }
    
        static final class FieldCfg implements FieldConfig {
    
            TypeDisplay typeDisplay = TypeDisplay.SIMPLE;
            Set<Visibility> visibilities = EnumSet.of(PROTECTED, PUBLIC);
    
            @Override
            public TypeDisplay typeDisplay() {
                return typeDisplay;
            }
    
            @Override
            public boolean include(Visibility visibility) {
                return visibilities.contains(visibility);
            }
        }
    
        static final class MethodCfg implements MethodConfig {
            // ParamNames paramNames = ParamNames.BEFORE_TYPE;
            ParamNames paramNames = ParamNames.NONE;
            TypeDisplay paramTypes = TypeDisplay.SIMPLE;
            TypeDisplay returnType = TypeDisplay.SIMPLE;
            Set<Visibility> visibilities = EnumSet.of(PROTECTED, PUBLIC);
            boolean javaBeanPropertiesAsFields = false;
    
            @Override
            public ParamNames paramNames() {
                return paramNames;
            }
    
            @Override
            public TypeDisplay paramTypes() {
                return paramTypes;
            }
    
            @Override
            public TypeDisplay returnType() {
                return returnType;
            }
    
            @Override
            public boolean include(Visibility visibility) {
                return visibilities.contains(visibility);
            }
    
            @Override
            public boolean javaBeanPropertiesAsFields() {
                return javaBeanPropertiesAsFields;
            }
        }
    }
