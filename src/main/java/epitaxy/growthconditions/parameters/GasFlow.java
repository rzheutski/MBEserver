package epitaxy.growthconditions.parameters;

/**
 * The class extends the abstract Precursor class. It is for gas sources.
 */
public class GasFlow extends Precursor {

    private double kgr;          // growth rate: Vgr = kgr*value

    public GasFlow() {
        super();
    }

    @Override
    protected double getGrowthRate(long timeStamp, double substrateTemperature) {
        return 0;
    }

}
