package epitaxy.structure;

import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.datatreatment.Approximation;
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

    private double pyrometerTemperature;

    //stoichiometry coefficients
    private double xInN; // Indium nitride mole fraction
    private double yAlN; // Aluminium nitride mole fraction

    private List<GrowthParameter> dopants;

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
        parameters = data.getGrowthParameters();
        initMaterial();
    }

    /**
     * It calculates all parameters of the material
     */
    private void initMaterial() {
        setActiveParameters();
        getStoichiometry();
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
     * It determines a chemical formula for the material and sets all its parameters.
     */
    private void getStoichiometry() {
        determinePyrometerTemperature();
        if ( (isPresent(gallium) | isPresent(aluminium) | isPresent(indium)) & isPresent(ammonia) ) ammoniaInAlGaN();

    }

    /**
     * It processes an InAlGaN grown by ammonia molecular beam epitaxy.
     */
    private void ammoniaInAlGaN() {

    }

    /**
     * It checks if a growth parameter is actual for the given layer
     * @param parameter growth parameter to be checked
     * @return true if the growth parameter is actual or false otherwise
     */
    private boolean isPresent(GrowthParameter parameter) {
        return (parameter.getValueAtTimeStamp(centerLayerTimeStamp) != null);
    }

    /**
     * It determines pyrometer temperature at the timeStamp accepted via constructor. A pyrometer temperature is a rather specific parameter which is calculated taking into account a substrate heat power.
     * @return pyrometer temparature value
     */
    private void determinePyrometerTemperature() {
        long startLayerTimeStamp = 0;
        long stopLayerTimeStamp = 0;
        for (int i = 1; i < substrateHeatPower.size(); i++) {
            if (substrateHeatPower.getTimeStamp(i) > centerLayerTimeStamp) {
                startLayerTimeStamp = substrateHeatPower.getTimeStamp(i-1);
                stopLayerTimeStamp = substrateHeatPower.getTimeStamp(i);
            }
        }
        if (substrateHeatPower.getValueAtTimeStamp(startLayerTimeStamp) == substrateHeatPower.getValueAtTimeStamp(stopLayerTimeStamp)) pyrometerTemperature = Approximation.getConstant(pyrometer, startLayerTimeStamp, stopLayerTimeStamp);
        else pyrometerTemperature = Approximation.getLinearFit(pyrometer, startLayerTimeStamp, stopLayerTimeStamp).getValue(timeStamp);

    }


}
