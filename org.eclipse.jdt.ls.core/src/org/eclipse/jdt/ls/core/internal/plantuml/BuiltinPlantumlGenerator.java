package org.eclipse.jdt.ls.core.internal.plantuml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.IOException;
import java.io.OutputStream;

public final class BuiltinPlantumlGenerator implements PlantumlGenerator {
    @Override
    public void generatePlantumlDiagramFromSource(String plantumlSource, FileFormat format, OutputStream out) throws IOException {
        new SourceStringReader(plantumlSource).outputImage(out, new FileFormatOption(format));
    }
}
