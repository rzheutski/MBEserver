package epitaxy.growthconditions.parameters;

/**
 * The class extends the abstract Precursor class. It is for gas sources.
 */
public class GasFlow extends Precursor {

    // growth rate: vGr = a*value + b
    private double a;
    private double b;

    public GasFlow() {
        super();
    }

    @Override
    /**
     * Returns growth rate value for the gas flow precursor at the given timestamp and substrate temperature.
     */
    public double getGrowthRate(long timeStamp, double substrateTemperature) {
        Double value = getValueAtTimeStamp(timeStamp);
        return ((value == null) || (value == 0))? 0 : (a + b*getValueAtTimeStamp(timeStamp));
    }

}
