package org.eclipse.jdt.ls.core.internal.plantuml;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormat;

public interface PlantumlGenerator {
    static PlantumlGenerator getPlantumlGenerator(Configuration configuration) {
        return configuration.plantumlServerUrl()
                .filter(url -> RemotePlantumlGenerator.HTTP_URLS.matcher(url).find())
                .map(url -> (PlantumlGenerator) new RemotePlantumlGenerator(url))
                .orElseGet(BuiltinPlantumlGenerator::new);
    }

    void generatePlantumlDiagramFromSource(String plantumlSource, FileFormat format, OutputStream out) throws IOException;

}
