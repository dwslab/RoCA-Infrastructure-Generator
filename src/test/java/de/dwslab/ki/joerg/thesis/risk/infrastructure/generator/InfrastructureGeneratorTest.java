package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class InfrastructureGeneratorTest {

    @Test
    public void testInfrastructureGenerator() {
        InfrastructureGenerator gen;
        for (int size = 100; size <= 100; size *= 10) {
            for (int cause = 1; cause <= 2; cause++) {
                for (int offline = 1; offline <= 5; offline++) {
                    System.out.println(size + " " + cause + " " + offline);
                    Path path = Paths.get("output/output_" + size + "_" + cause + "_" + offline + ".db");
                    gen = new InfrastructureGenerator(0, path, size, cause, offline);
                    gen.generate();
                }
            }
        }
    }

}
