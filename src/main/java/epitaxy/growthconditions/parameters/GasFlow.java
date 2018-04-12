package epitaxy.growthconditions.parameters;

/**
 * The class extends the abstract Precursor class. It is for gas sources.
 */
public class GasFlow extends Precursor {

    // growth rate: vGr = koeff*value
    private double koeff;

    public GasFlow() {
        super();
    }

    @Override
    public double getGrowthRate(long timeStamp, double substrateTemperature) {
        return koeff*getValueAtTimeStamp(timeStamp);
    }

}
