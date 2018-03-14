package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator;

import java.util.Random;

import org.junit.Test;

public class GaussianTest {

    @Test
    public void testGaussianParameters() {
        Random rnd = new Random();

        double stdDev = 0.1;
        double mean = 0.05;

        for (int i = 0; i < 10; i++) {
            System.out.println(rnd.nextGaussian() * stdDev + mean);
        }

    }

}
