package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator;

import java.util.ArrayList;
import java.util.List;

import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.Component;
import de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components.PowerSource;

public class Infrastructure {

    private final List<PowerSource> powerSources = new ArrayList<>();
    private final List<Component> networks = new ArrayList<>();
    private final List<Component> hardwares = new ArrayList<>();
    private final List<Component> services = new ArrayList<>();
    private final List<Component> rootCauses = new ArrayList<>();
    private final List<Component> offlines = new ArrayList<>();
    private final List<Component> notOfflines = new ArrayList<>();

    public void addPowerSource(PowerSource source) {
        powerSources.add(source);
    }

    public List<PowerSource> getPowerSources() {
        return powerSources;
    }

    public void addNetworks(Component network) {
        networks.add(network);
    }

    public List<Component> getNetworks() {
        return networks;
    }

    public void addHardware(Component hardware) {
        hardwares.add(hardware);
    }

    public List<Component> getHardwares() {
        return hardwares;
    }

    public void addService(Component service) {
        services.add(service);
    }

    public List<Component> getServices() {
        return services;
    }

    public void addRootCauses(Component rootCause) {
        rootCauses.add(rootCause);
    }

    public List<Component> getRootCauses() {
        return rootCauses;
    }

    public void addOffline(Component offline) {
        offlines.add(offline);
    }

    public List<Component> getOfflines() {
        return offlines;
    }

    public void addNotOffline(Component notOffline) {
        notOfflines.add(notOffline);
    }

    public List<Component> getNotOfflines() {
        return notOfflines;
    }

    public int size() {
        return getPowerSources().size() + getNetworks().size() + getHardwares().size() + getServices().size();
    }

}
