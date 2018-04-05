package epitaxy.growthconditions.parameters;

/**
 * The class extends the abstract GrowthParameterWithShutter class. It is for gas sources.
 */
public class GasFlow extends GrowthParameterWithShutter {

    private double kgr;          // growth rate: Vgr = kgr*value

    public GasFlow() {
        super();
    }

}
