package epitaxy.growthconditions.parameters;

/**
 * The class extends the abstract Precursor class. It is for Knudsen effusion cell.
 */
public class EffusionCell extends Precursor {

    private double effusKoeff;          // effusion rate: rateFlow = effusKoeff*Exp(-effusTemperature/value)
    private double effusTemperature;
    private double desorpKoeff;          // desorption rate: rateDes = desorpKoeff*Exp(desorpTemperature/substrateTemperature)
    private double desorpTemperature;

    public EffusionCell() {
        super();
    }

    @Override
    public double getGrowthRate(long timeStamp, double substrateTemperature) {
        return (effusKoeff*Math.exp(-effusTemperature/getValueAtTimeStamp(timeStamp)) - desorpKoeff*Math.exp(desorpTemperature/substrateTemperature));
    }

}
