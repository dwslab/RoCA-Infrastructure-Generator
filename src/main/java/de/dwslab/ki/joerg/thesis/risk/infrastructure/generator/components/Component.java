package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components;

import java.util.ArrayList;
import java.util.List;

public class Component {

    public static final String NAME_PREFIX = "Component_";
    public static long NEXT_ID = 1;

    private final long id;

    private final int depth;
    private final List<Component> dependencies = new ArrayList<>();
    private final List<Component> dependents = new ArrayList<>();
    private final List<Risk> risks = new ArrayList<>();

    public static long getNextId() {
        return NEXT_ID;
    }

    public Component(int depth) {
        this(NEXT_ID++, depth);
    }

    public Component(long id, int depth) {
        this.id = id;
        this.depth = depth;
    }

    public long getId() {
        return id;
    }

    public int getDepth() {
        return depth;
    }

    public String getName() {
        return NAME_PREFIX + id;
    }

    public void addDependency(Component component) {
        dependencies.add(component);
    }

    public List<Component> getDependencies() {
        return dependencies;
    }

    public void addDependent(Component component) {
        dependents.add(component);
    }

    public List<Component> getDependents() {
        return dependents;
    }

    public void addRisks(List<Risk> risks) {
        this.risks.addAll(risks);
    }

    public List<Risk> getRisks() {
        return risks;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ id >>> 32);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Component other = (Component) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }

}
