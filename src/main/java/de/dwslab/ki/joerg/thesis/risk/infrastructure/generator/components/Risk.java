package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components;

public class Risk {

    public static final String NAME_PREFIX = "Risk_";
    public static long NEXT_ID = 1;

    private final long id;
    private final double probability;

    public static long getNextId() {
        return NEXT_ID;
    }

    public Risk(double probability) {
        this.id = NEXT_ID++;
        this.probability = probability;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return NAME_PREFIX + id;
    }

    public double getProbability() {
        return probability;
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
        Risk other = (Risk) obj;
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
