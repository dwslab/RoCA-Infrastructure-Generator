package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator.components;

public class PowerSource extends Component {

    public static final String NAME_PREFIX = "PowerSource_";
    public static long NEXT_ID = 1;

    public static long getNextId() {
        return NEXT_ID;
    }

    public PowerSource() {
        super(NEXT_ID++, 0);
    }

    @Override
    public String getName() {
        return NAME_PREFIX + getId();
    }

    @Override
    public void addDependency(Component component) {
        throw new RuntimeException("Cannot add a dependency to a PowerSource");
    }

    @Override
    public String toString() {
        return getName();
    }

}
