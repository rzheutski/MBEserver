package epitaxy.structure;

import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.parameters.GrowthParameter;

import java.util.List;

/**
 * This class composes a nitride material and its properties using a set of growth parameters.
 */
public class Material {

    // all possible growth parameters at nitride molecular beam epitaxy
    private GrowthParameter gallium;
    private GrowthParameter aluminium;
    private GrowthParameter indium;
    private GrowthParameter silicon;
    private GrowthParameter magnesium;
    private GrowthParameter nitrogenPlasma;
    private GrowthParameter ammonia;
    private GrowthParameter silane;
    private GrowthParameter substrateHeatPower;
    private GrowthParameter substrateTemperature;
    private GrowthParameter pyrometer;

    private long timeStamp;
    private Data data;
    private List<GrowthParameter> parameters;
    private long centerLayerTimeStamp;

    public Material(Data data, long timeStamp) {
        this(data, timeStamp, 0);
    }

    /**
     *
     * @param data
     * @param timeStamp
     * @param centerLayerTimeStamp
     */
    public Material(Data data, long timeStamp, long centerLayerTimeStamp) {
        this.data = data;
        this.timeStamp = timeStamp;
        this.centerLayerTimeStamp = centerLayerTimeStamp;
        initMaterial();
    }

    /**
     * It calculates all parameters of the material
     */
    private void initMaterial() {
        setActiveParameters();
        determineMaterial();
    }

    /**
     * It sets all actual precursors for this material. A precursor remains null if it is not actual for the current material.
     */
    private void setActiveParameters() {
        for (GrowthParameter parameter : parameters) {
            if (parameter.getName().contains("Ga")) gallium = parameter;
            else if (parameter.getName().contains("Al")) aluminium = parameter;
            else if (parameter.getName().contains("In")) indium = parameter;
            else if (parameter.getName().contains("Si")) silicon = parameter;
            else if (parameter.getName().contains("Mg")) magnesium = parameter;
            else if (parameter.getName().contains("N2")) nitrogenPlasma = parameter;
            else if (parameter.getName().contains("NH3")) ammonia = parameter;
            else if (parameter.getName().contains("SiH4")) silane = parameter;
            else if (parameter.getName().toLowerCase().contains("power")) substrateHeatPower = parameter;
            else if (parameter.getName().toLowerCase().contains("temp")) substrateTemperature = parameter;
            else if (parameter.getName().toLowerCase().contains("pyro")) pyrometer = parameter;
        }
    }

    /**
     * It determines a chemical formula for the material and seta all its parameters.
     */
    private void determineMaterial() {
        if ( (isPresent(gallium) | isPresent(aluminium) | isPresent(indium)) & isPresent(ammonia) ) ammoniaInAlGaN();
    }

    /**
     * It processes an InAlGaN grown by ammonia molecular beam epitaxy.
     */
    private void ammoniaInAlGaN() {

    }

    private boolean isPresent(GrowthParameter parameter) {
        return (parameter != null);
    }


}
