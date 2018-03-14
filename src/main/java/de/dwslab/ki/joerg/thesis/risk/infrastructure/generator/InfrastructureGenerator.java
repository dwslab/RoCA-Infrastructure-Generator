package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.Component;
import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.PowerSource;
import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.Risk;

public class InfrastructureGenerator {

    private static final int MAX_DEPTH = 14;

    private final Random random;
    private final Path outputFolder;
    private final int size;
    private final Infrastructure model;
    private final int numRootCauses;
    private final int numOfflinePerCause;

    public InfrastructureGenerator(long seed, Path outputFolder, int size, int numRootCauses, int numOfflinePerCause) {
        this.outputFolder = outputFolder;
        this.size = size;
        this.numRootCauses = numRootCauses;
        this.numOfflinePerCause = numOfflinePerCause;
        this.random = new Random(seed);
        this.model = new Infrastructure();

        // FIXME not nice... not at all...
        Component.NEXT_ID = PowerSource.NEXT_ID = Risk.NEXT_ID = 1;
    }

    /*
     * 1-3 services per server
     * 16-32 servers per switch
     *
     * n := switch
     * s := server
     * a := service
     *
     * 24 * n = s
     * 2 * s = a
     * n + s + a = total
     *
     * 48 * n = a
     *
     * n + 24n + 48n = total
     * 73n = total
     * total / 73 = n
     *
     * s/24 + s + 2s = total
     * s + 24s + 48s = 24 total
     * s = 24/73 * total
     *
     * a/48 + a/2 + a = total
     * a + 24a + 48a = 48total
     * 73a = 48total
     * a = 48 / 73 * total
     */

    public void generate() {
        System.out.println("Generating power sources...");
        generatePowerSources();
        System.out.println("...done!\nGenerating network components...");
        generateNetworkComponents();
        System.out.println("...done!\nGenerating servers...");
        generateServers();
        System.out.println("...done!\nGenerating services...");
        generateServices();
        System.out.println("...done!\nGenerating offline components...");
        generateOfflineComponents();
        // System.out.println("...done!\nGenerating not offline components...");
        // generateNotOfflineComponents();
        System.out.println("...done!");

        System.out.println("Writing model...");
        MlnInfrastructureOutput out = new MlnInfrastructureOutput(outputFolder);
        out.printModel(model);
        System.out.println("done!");
    }

    /**
     * Should be very few. Every PowerSource could more or less be interpreted as different physical location.
     *
     * @return
     */
    private void generatePowerSources() {
        int num = (int) Math.round(Math.max(1, Math.log10(size) - 1));
        for (int i = 0; i < num; i++) {
            PowerSource source = new PowerSource();
            source.addRisks(generateRisks());
            model.addPowerSource(source);
        }
    }

    private PowerSource assignPowerSource() {
        int numPower = model.getPowerSources().size();
        PowerSource[] sources = model.getPowerSources().toArray(new PowerSource[numPower]);
        return sources[(int) generateRandom(0, numPower)];
    }

    /**
     * Dependent on one single node connecting to the internet.
     *
     * @return
     */
    private void generateNetworkComponents() {
        double mean = Math.max(0, size / 73d);
        double stdDev = Math.log10(size);
        int numNetwork = (int) Math.round(generateGaussian(mean, stdDev));
        for (int i = 0; i < numNetwork; i++) {
            Component network;
            // the first network component does not have any dependencies, all others build a tree
            if (i != 0) {
                Component parent = assignNetwork();
                network = new Component(parent.getDepth() + 1);
                network.addDependency(parent);
                parent.addDependent(network);
            } else {
                // depth == 1 because power source have 0
                network = new Component(1);
            }
            PowerSource power = assignPowerSource();
            network.addDependency(power);
            power.addDependent(network);
            network.addRisks(generateRisks());
            model.addNetworks(network);
        }
    }

    /**
     * May be interdependent.
     */
    private void generateServers() {
        double mean = Math.max(0, size * 24 / 73d);
        double stdDev = Math.log10(size);
        int numServers = (int) Math.round(generateGaussian(mean, stdDev));
        for (int i = 0; i < numServers; i++) {
            PowerSource power = assignPowerSource();
            Component network = assignNetwork();

            Component server = new Component(network.getDepth());
            server.addRisks(generateRisks());
            server.addDependency(power);
            server.addDependency(network);
            // TODO assign service (e.g. NFS)?
            model.addHardware(server);
        }
    }

    /**
     * Depending on a few servers, and any other services.
     */
    private void generateServices() {
        double mean = Math.max(0, size * 48 / 73d);
        double stdDev = Math.log10(size);
        int numServices = (int) Math.round(generateGaussian(mean, stdDev));
        for (int i = 0; i < numServices; i++) {
            Component server = assignServer();
            Component service = new Component(server.getDepth());
            service.addRisks(generateRisks());
            service.addDependency(server);

            if (i != 0) {
                int numDepServices = (int) Math.round(generateGaussian(2, 0.5));
                for (int j = 0; j < numDepServices; j++) {
                    service.addDependency(assignService());
                }
            }
            model.addService(service);
        }

    }

    private void generateOfflineComponents() {
        int numPower = model.getPowerSources().size();
        int numNetworks = model.getNetworks().size();
        int numHardwares = model.getHardwares().size();
        int numServices = model.getServices().size();

        // double numOffline = generateGaussian(2, 0.5);
        while (model.getRootCauses().size() < numRootCauses) {
            int numOffline = (int) generateGaussian(0, 0.5 * model.size(), 0, model.size());
            Component cause;
            if (numOffline < numPower) {
                cause = model.getPowerSources().get(numOffline);
            } else if (numOffline < numPower + numNetworks) {
                cause = model.getNetworks().get(numOffline - numPower);
            } else if (numOffline < numPower + numNetworks + numHardwares) {
                cause = model.getHardwares().get(numOffline - numPower - numNetworks);
            } else if (numOffline < numPower + numNetworks + numHardwares + numServices) {
                cause = model.getServices().get(numOffline - numPower - numNetworks - numHardwares);
            } else {
                throw new RuntimeException("Wrong random boundaries");
            }
            Set<Component> dependents = new HashSet<>();
            collectDependents(cause, dependents);
            List<Component> dependentsList = new ArrayList<>(dependents);
            Set<Component> offlines = new HashSet<>();
            if (dependentsList.size() >= numOfflinePerCause) {
                while (offlines.size() < numOfflinePerCause) {
                    Component offline = dependentsList.get((int) generateRandom(0, dependentsList.size()));
                    offlines.add(offline);
                }
                offlines.stream().forEach(model::addOffline);
                model.addRootCauses(cause);
            }
        }
    }

    private void collectDependents(Component root, Set<Component> dependents) {
        for (Component comp : root.getDependents()) {
            dependents.add(comp);
            collectDependents(comp, dependents);
        }
    }

    private void generateNotOfflineComponents() {
        int numPower = model.getPowerSources().size();
        int numNetworks = model.getNetworks().size();
        int numHardwares = model.getHardwares().size();
        int numServices = model.getServices().size();

        double numNotOffline = generateGaussian(2, 0.5);
        for (int i = 0; i < numNotOffline; i++) {
            int offline = (int) generateRandom(0, model.size());
            if (offline < numPower) {
                PowerSource powerSource = model.getPowerSources().get(offline);
                model.addNotOffline(powerSource);
            } else if (offline < numPower + numNetworks) {
                Component component = model.getNetworks().get(offline - numPower);
                model.addNotOffline(component);
            } else if (offline < numPower + numNetworks + numHardwares) {
                Component component = model.getHardwares().get(offline - numPower - numNetworks);
                model.addNotOffline(component);
            } else if (offline < numPower + numNetworks + numHardwares + numServices) {
                Component component = model.getServices().get(offline - numPower - numNetworks - numHardwares);
                model.addNotOffline(component);
            } else {
                throw new RuntimeException("Wrong random boundaries");
            }
        }
        model.getNotOfflines().removeAll(model.getOfflines());
    }

    // TODO switch to gaussian
    private Component assignNetwork() {
        List<Component> networks = model.getNetworks();
        Component component;
        do {
            component = networks.get((int) generateRandom(0, networks.size()));
        } while (component.getDepth() > MAX_DEPTH);
        return component;
    }

    // TODO switch to gaussian
    private Component assignServer() {
        List<Component> servers = model.getHardwares();
        Component component;
        do {
            component = servers.get((int) generateRandom(0, servers.size()));
        } while (component.getDepth() > MAX_DEPTH);
        return component;
    }

    // TODO switch to gaussian
    private Component assignService() {
        List<Component> services = model.getServices();
        Component component;
        do {
            component = services.get((int) generateRandom(0, services.size()));
        } while (component.getDepth() > MAX_DEPTH);
        return component;
    }

    private List<Risk> generateRisks() {
        List<Risk> threats = new ArrayList<>();
        for (int i = 0; i < generateRiskCount(); i++) {
            threats.add(new Risk(generateRiskProbability()));
        }
        return threats;
    }

    private double generateRiskCount() {
        return generateGaussian(1, 3, 1, 10);
    }

    private double generateRiskProbability() {
        return generateGaussian(0.1, 0.05, 0, 0.5);
    }

    private double generateRandom(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    private double generateGaussian(double mean, double stdDev) {
        return Math.round(generateGaussian(mean, stdDev, 1, Integer.MAX_VALUE));
    }

    private double generateGaussian(double mean, double stdDev, double min, double max) {
        double prob = min - 1;
        while (prob < min || max < prob) {
            prob = random.nextGaussian() * stdDev + mean;
        }
        return prob;
    }

}
