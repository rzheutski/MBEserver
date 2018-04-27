package epitaxy.growthconditions.parameters;

/**
 * The class extends the abstract Precursor class. It is for Knudsen effusion cell.
 */
public class EffusionCell extends Precursor {

    // growth rate: vGr = vFlow - vDes;
    private double effusKoeff;          // effusion rate: vFlow = effusKoeff*Exp(-effusTemperature/value)
    private double effusTemperature;
    private double desorpKoeff;          // desorption rate: vDes = desorpKoeff*Exp(desorpTemperature/substrateTemperature)
    private double desorpTemperature;

    public EffusionCell() {
        super();
    }

    @Override
    public double getGrowthRate(long timeStamp, double substrateTemperature) {
        Double value = getValueAtTimeStamp(timeStamp);
        return ((value == null) || (value == 0))? 0 : (effusKoeff*Math.exp(-effusTemperature/getValueAtTimeStamp(timeStamp)) - desorpKoeff*Math.exp(desorpTemperature/substrateTemperature));
    }

    /**
     * Returns value of growth rate for the precursor at the given time stamp and substrate temperature taking into account desorption weighting factor
     * @param timeStamp time stamp for the growth rate got
     * @param substrateTemperature value of substrate temperature
     * @param desorptionFactor desorption weighting factor which multiplies desorption rate
     * @return growth rate value in um/hour units
     */
    public double getGrowthRate(long timeStamp, double substrateTemperature, double desorptionFactor) {
        Double value = getValueAtTimeStamp(timeStamp);
        return ((value == null) || (value == 0))? 0 : (effusKoeff*Math.exp(-effusTemperature/getValueAtTimeStamp(timeStamp)) - desorptionFactor*desorpKoeff*Math.exp(desorpTemperature/substrateTemperature));
    }

}
