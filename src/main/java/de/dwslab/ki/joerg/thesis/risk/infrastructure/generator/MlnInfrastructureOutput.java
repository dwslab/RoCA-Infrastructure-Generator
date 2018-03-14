package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;

import org.jooq.lambda.Unchecked;

import de.dwslab.ai.util.MLN;
import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.Component;
import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.PowerSource;
import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.Risk;

public class MlnInfrastructureOutput {

    private static final String DEPENDS_ON = "dependsOn";
    private static final String HAS_RISK = "hasRiskDegree";
    private static final String INFRA = "infra";
    private static final String RISK = "risk";
    private static final String OFFLINE = "offline";

    private final Path output;

    public MlnInfrastructureOutput(Path output) {
        this.output = output;
    }

    public void printModel(Infrastructure infrastructure) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(output.toFile()))) {
            // Print all concepts
            for (int i = 1; i < Component.getNextId(); i++) {
                printComponent(Component.NAME_PREFIX + i, out);
            }
            for (int i = 1; i < PowerSource.getNextId(); i++) {
                printComponent(PowerSource.NAME_PREFIX + i, out);
            }
            for (int i = 1; i < Risk.getNextId(); i++) {
                printRisk(Risk.NAME_PREFIX + i, out);
            }

            // Print all relations
            for (Component component : infrastructure.getNetworks()) {
                printDependencies(component, component.getDependencies(), out);
                printHasRisks(component, component.getRisks(), out);
            }
            for (Component component : infrastructure.getHardwares()) {
                printDependencies(component, component.getDependencies(), out);
                printHasRisks(component, component.getRisks(), out);
            }
            for (Component component : infrastructure.getServices()) {
                printDependencies(component, component.getDependencies(), out);
                printHasRisks(component, component.getRisks(), out);
            }

            // Print evidence
            for (Component component : infrastructure.getOfflines()) {
                printOffline(component, out);
            }
            for (Component component : infrastructure.getNotOfflines()) {
                printNotOffline(component, out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printComponent(String name, BufferedWriter out) throws IOException {
        out.write(INFRA + "(\"");
        out.write(name);
        out.write("\")\n");
    }

    private void printRisk(String name, BufferedWriter out) throws IOException {
        out.write(RISK + "(\"");
        out.write(name);
        out.write("\")\n");
    }

    private void printDependencies(Component source, Collection<Component> targets, BufferedWriter out) {
        targets.stream().forEach(Unchecked.consumer(t -> {
            out.write(DEPENDS_ON + "(\"");
            out.write(source.getName());
            out.write("\",\"");
            out.write(t.getName());
            out.write("\")\n");
        }));
    }

    private void printHasRisks(Component component, Collection<Risk> risks, BufferedWriter out) {
        risks.stream().forEach(Unchecked.consumer(r -> {
            double weight = MLN.logit(r.getProbability());
            String weightStr = String.format(Locale.US, "%.4f", weight);

            out.write(HAS_RISK + "(\"");
            out.write(component.getName());
            out.write("\",\"");
            out.write(r.getName());
            out.write("\",");
            out.write(weightStr);
            out.write(")\n");
        }));
    }

    private void printOffline(Component component, BufferedWriter out) throws IOException {
        out.write(OFFLINE + "(\"");
        out.write(component.getName());
        out.write("\")\n");
    }

    private void printNotOffline(Component component, BufferedWriter out) throws IOException {
        out.write("!" + OFFLINE + "(\"");
        out.write(component.getName());
        out.write("\")\n");
    }

}
