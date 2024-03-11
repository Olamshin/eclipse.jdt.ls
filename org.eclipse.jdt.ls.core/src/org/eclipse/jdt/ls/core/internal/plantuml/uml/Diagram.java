package org.eclipse.jdt.ls.core.internal.plantuml.uml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.jdt.ls.core.internal.plantuml.Configuration;
import org.eclipse.jdt.ls.core.internal.plantuml.PlantumlGenerator;
import org.eclipse.jdt.ls.core.internal.plantuml.configuration.ImageConfig;
import org.eclipse.jdt.ls.core.internal.plantuml.logging.Message;
import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.IndentingPrintWriter;
import org.eclipse.jdt.ls.core.internal.plantuml.rendering.writers.StringBufferingWriter;

import net.sourceforge.plantuml.FileFormat;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.eclipse.jdt.ls.core.internal.plantuml.FileUtils.ensureParentDir;
import static org.eclipse.jdt.ls.core.internal.plantuml.FileUtils.relativePath;
import static org.eclipse.jdt.ls.core.internal.plantuml.FileUtils.withoutExtension;

public abstract class Diagram extends UMLNode {

    private final Configuration config;
    private final PlantumlGenerator plantumlGenerator;
    private final FileFormat[] formats;
    private File diagramBaseFile;
    private String _plantUmlSource;

    protected Diagram(Configuration config) {
        super(null);
        this.config = requireNonNull(config, "Configuration is <null>");
        this.plantumlGenerator = PlantumlGenerator.getPlantumlGenerator(config);
        this.formats = config.images().formats().stream()
                .map(this::toFileFormat).filter(Objects::nonNull)
                .toArray(FileFormat[]::new);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        output.append("@startuml").newline();
        IndentingPrintWriter indented = output.indent();
        writeCustomDirectives(config.customPlantumlDirectives(), indented);
        writeChildrenTo(indented);
        writeFooterTo(indented);
        output.append("@enduml").newline();
        return output;
    }

    protected <IPW extends IndentingPrintWriter> IPW writeCustomDirectives(List<String> customDirectives, IPW output) {
        customDirectives.forEach(output::println);
        if (!customDirectives.isEmpty()) {
            output.newline();
        }
        return output;
    }

    private <IPW extends IndentingPrintWriter> IPW writeFooterTo(IPW output) {
        output.append("center footer").whitespace()
                .append(net.sourceforge.plantuml.version.Version.versionString())
                .newline();
        // TODO:
                // .append(config.logger().localize(
                //         net.sourceforge.plantuml.version.Version.versionString()))
                        
        return output;
    }

    public Configuration getConfiguration() {
        return config;
    }

    /**
     * Determine the physical file location for the plantuml output.
     *
     * <p>This will even be called if {@code -createPumlFiles} is not enabled,
     * to determine the {@code diagram base file}.
     *
     * @return The physical file for the plantuml output.
     */
    protected abstract File getPlantUmlFile();

    /**
     * @return The diagram file without extension.
     * @see #getDiagramFile(FileFormat)
     */
    private File getDiagramBaseFile() {
        if (diagramBaseFile == null) {
            File destinationDir = new File(config.destinationDirectory());
            String relativeBaseFile = withoutExtension(relativePath(destinationDir, getPlantUmlFile()));
            if (config.images().directory().isPresent()) {
                File imageDir = new File(destinationDir, config.images().directory().get());
                diagramBaseFile = new File(imageDir, relativeBaseFile.replace('/', '.'));
            } else {
                diagramBaseFile = new File(destinationDir, relativeBaseFile);
            }
        }
        return diagramBaseFile;
    }

    /**
     * The diagram file in the specified format.
     *
     * @param format The diagram file format.
     * @return The diagram file.
     */
    private File getDiagramFile(FileFormat format) {
        File base = getDiagramBaseFile();
        return new File(base.getParent(), base.getName() + format.getFileSuffix());
    }

    public void render() {
        try {
            // 1. Render UML sources
            String plantumlSource = renderPlantumlSource();
            if (Link.linkFrom(getDiagramBaseFile().getParent()) || plantumlSource == null) {
                plantumlSource = super.toString(); // Must re-render in case of different link base paths.
            }

            if (config.renderPumlFile()) {
                // 2. Render each diagram.
                for (FileFormat format : formats) {
                    renderDiagramFile(plantumlSource, format);
                }
            } else {
                _plantUmlSource = plantumlSource;
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("I/O error rendering " + this + ": " + ioe.getMessage(), ioe);
        } finally {
            Link.linkFrom(null);
        }
    }

    private String renderPlantumlSource() throws IOException {
        if (config.renderPumlFile()) {
            return writePlantumlSourceToFile();
        } else {
            return writePlantumlSource();
        }
    }

    public String getPlantumlSource() {
        return _plantUmlSource;
    }

    private String writePlantumlSourceToFile() throws IOException {
        File pumlFile = getPlantUmlFile();
        config.logger().info(Message.INFO_GENERATING_FILE, pumlFile);

        ensureParentDir(pumlFile);
        Link.linkFrom(pumlFile.getParent());
        try (StringBufferingWriter writer = createBufferingPlantumlFileWriter(pumlFile)) {
            writeTo(IndentingPrintWriter.wrap(writer, config.indentation()));
            return writer.getBuffer().toString();
        }
    }

    private String writePlantumlSource() throws IOException {
        config.logger().info(Message.INFO_GENERATING_SOURCE);

        //Link.linkFrom(pumlFile.getParent());
        try (StringBufferingWriter writer = createBufferingPlantumlStringWriter()) {
            writeTo(IndentingPrintWriter.wrap(writer, config.indentation()));
            return writer.getBuffer().toString();
        }
    }

    private StringBufferingWriter createBufferingPlantumlFileWriter(File pumlFile) throws IOException {
        return new StringBufferingWriter(
                new OutputStreamWriter(
                        Files.newOutputStream(pumlFile.toPath()), config.umlCharset()));
    }

    private StringBufferingWriter createBufferingPlantumlStringWriter() throws IOException {
        return new StringBufferingWriter();
    }

    private void renderDiagramFile(String plantumlSource, FileFormat format) throws IOException {
        final File diagramFile = getDiagramFile(format);
        config.logger().info(Message.INFO_GENERATING_FILE, diagramFile);
        ensureParentDir(diagramFile);
        try (OutputStream out = Files.newOutputStream(diagramFile.toPath())) {
            plantumlGenerator.generatePlantumlDiagramFromSource(plantumlSource, format, out);
        }
    }

    @Override
    public String toString() {
        final String name = getDiagramBaseFile().getPath();
        if (formats.length == 1) return name + formats[0].getFileSuffix();
        return name + Stream.of(formats).map(FileFormat::getFileSuffix)
                .map(s -> s.substring(1))
                .collect(joining(",", ".[", "]"));
    }

    /**
     * Static utility method to convert an image format to PlantUML {@linkplain FileFormat} with the same name.
     *
     * @param format The image format to convert into PlantUML fileformat.
     * @return The PlantUML file format.
     */
    private FileFormat toFileFormat(ImageConfig.Format format) {
        try {
            switch (format) {
                case SVG:
                case SVG_IMG:
                    return FileFormat.SVG;
                default:
                    return FileFormat.valueOf(format.name());
            }
        } catch (RuntimeException incompatibleFormatOrNull) {
            config.logger().debug(Message.WARNING_UNRECOGNIZED_IMAGE_FORMAT, format);
        }
        return null;
    }

}
