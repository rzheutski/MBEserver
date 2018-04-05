package epitaxy.growthconditions.parameters;

/**
 * The class extends the abstract GrowthParameterWithShutter class. It is for Knudsen effusion cell.
 */
public class EffusionCell extends GrowthParameterWithShutter {

    public double getAad() {
        return aad;
    }

    private double aad;          // adsorption rate: Vad = aad*Exp(kad*value)
    private double kad;
    private double ades;          // desorption rate: Vdes = ades*Exp(kdes/substrateTemperature)
    private double kdes;

    public EffusionCell() {
        super();
    }

}
