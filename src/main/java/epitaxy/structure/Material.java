package epitaxy.structure;

import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.datatreatment.Approximation;
import epitaxy.growthconditions.parameters.EffusionCell;
import epitaxy.growthconditions.parameters.GasFlow;
import epitaxy.growthconditions.parameters.GrowthParameter;
import epitaxy.growthconditions.parameters.SubstrateHeat;

import java.util.List;

/**
 * This class composes a nitride material and its properties using a set of growth parameters.
 */
public class Material {

    private static final String SILICON_DOPANT = "Si";
    private static final String MAGNESIUM_DOPANT = "Mg";

    // all possible growth parameters at nitride molecular beam epitaxy
    private EffusionCell gallium;
    private EffusionCell aluminium;
    private EffusionCell indium;
    private EffusionCell silicon;
    private EffusionCell magnesium;
    private GasFlow nitrogenPlasma;
    private GasFlow ammonia;
    private GasFlow silane;
    private SubstrateHeat substrateHeaterPower;
    private SubstrateHeat substrateHeaterTemperature;
    private SubstrateHeat pyrometer;

    private long timeStamp;
    private Data data;
    private List<GrowthParameter> parameters;
    private long centerLayerTimeStamp;

    private double pyrometerTemperature;
    private double heaterPower;
    private double heaterTemperature;

    private double xInN; // Indium nitride mole fraction
    private double yAlN; // Aluminium nitride mole fraction

    private double growthRate;
    private MaterialType materialType;
    private boolean metalRichConditions;

    private List<String> dopants;
    private List<Double> dopingLevels;

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
        determineParameters();
        determineStoichiometry();
        determineDopants();
    }

    /**
     * It sets all actual precursors for this material. A precursor remains null if it is not actual for the current material.
     */
    private void determineParameters() {
        for (GrowthParameter parameter : parameters) {
            if (parameter.getName().contains("Ga")) gallium = (EffusionCell)parameter;
            else if (parameter.getName().contains("Al")) aluminium = (EffusionCell)parameter;
            else if (parameter.getName().contains("In")) indium = (EffusionCell)parameter;
            else if (parameter.getName().contains("Si")) silicon = (EffusionCell)parameter;
            else if (parameter.getName().contains("Mg")) magnesium = (EffusionCell)parameter;
            else if (parameter.getName().contains("N2")) nitrogenPlasma = (GasFlow)parameter;
            else if (parameter.getName().contains("NH3")) ammonia = (GasFlow)parameter;
            else if (parameter.getName().contains("SiH4")) silane = (GasFlow)parameter;
            else if (parameter.getName().toLowerCase().contains("power")) substrateHeaterPower = (SubstrateHeat)parameter;
            else if (parameter.getName().toLowerCase().contains("temp")) substrateHeaterTemperature = (SubstrateHeat)parameter;
            else if (parameter.getName().toLowerCase().contains("pyro")) pyrometer = (SubstrateHeat)parameter;
        }
    }

    /**
     * It determines a chemical formula for the material and sets all its parameters.
     */
    private void determineStoichiometry() {
        determineSubstrateTemperature();
        if ((isPresent(indium) | isPresent(aluminium) | isPresent(gallium)) & (isPresent(ammonia) | isPresent(nitrogenPlasma))) InAlGaN();
        else if (isPresent(indium) | isPresent(aluminium) | isPresent(gallium)) metal();
        else if ((isPresent(silane) | isPresent(silicon)) & (isPresent(ammonia) | isPresent(nitrogenPlasma))) SiN();
        else if (isPresent(ammonia)) ammonia();
        else if (isPresent(nitrogenPlasma)) nitrogenPlasma();
        else empty();


    }

    /**
     * It processes an InAlGaN grown either by ammonia molecular beam epitaxy or by plasma molecular beam epitaxy.
     */
    private void InAlGaN() {
        materialType = MaterialType.InAlN;
        double vIn = indium.getGrowthRate(timeStamp, pyrometerTemperature);
        double vAl = aluminium.getGrowthRate(timeStamp, pyrometerTemperature);
        double vGa = gallium.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNH3 = ammonia.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNplasma = nitrogenPlasma.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNitrogen = (vNH3 == 0)? vNplasma : vNH3;
        if ((vIn + vAl + vGa) < vNitrogen) {
            // nitrogen-rich conditions
            metalRichConditions = false;
            growthRate = vIn + vAl + vGa;
            xInN = vIn/growthRate;
            yAlN = vAl/growthRate;

        }
        else {
            // metal-rich conditions
            metalRichConditions = true;
            growthRate = vNitrogen;
            xInN = Math.max(0, (growthRate - vAl - vGa)/growthRate);
            yAlN = Math.min(1, vAl/growthRate);

        }
    }

    /**
     * It processes a metal-deposited layer.
     */
    private void metal() {
        materialType = MaterialType.METAL;
        double vIn = indium.getGrowthRate(timeStamp, pyrometerTemperature);
        double vAl = aluminium.getGrowthRate(timeStamp, pyrometerTemperature);
        double vGa = gallium.getGrowthRate(timeStamp, pyrometerTemperature);
        growthRate = vIn + vAl + vGa;
    }

    /**
     * It processes a silicon nitride layer.
     */
    private void SiN() {
        materialType = MaterialType.SiN;
        double vSilicon = silicon.getGrowthRate(timeStamp, pyrometerTemperature);
        double vSilane = silane.getGrowthRate(timeStamp, pyrometerTemperature);
        double vSi = (vSilicon == 0)? vSilane : vSilicon;
        double vNH3 = ammonia.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNplasma = nitrogenPlasma.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNitrogen = (vNH3 == 0)? vNplasma : vNH3;
        // A growth rate function is unknown now, therefore it is accepted as zero for the moment.
        growthRate = 0;

    }

    /**
     * It processes an imaginary layer corresponding to exposing of the sample to an ammonia flow.
     */
    private void ammonia() {
        double vNH3 = ammonia.getGrowthRate(timeStamp, pyrometerTemperature);
        growthRate = vNH3;
        materialType = MaterialType.NH3;
    }

    /**
     * It processes an imaginary layer corresponding to exposing of the sample to a nitrogen plasma flow.
     */
    private void nitrogenPlasma() {
        double vNplasma = nitrogenPlasma.getGrowthRate(timeStamp, pyrometerTemperature);
        growthRate = vNplasma;
        materialType = MaterialType.NITROGEN_PLASMA;
    }

    /**
     * It processes a case of absence of an active precursors
     */
    private void empty() {
        materialType = MaterialType.EMPTY;
        growthRate = 0;
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
    private void determineSubstrateTemperature() {
        long startLayerTimeStamp = 0;
        long stopLayerTimeStamp = 0;
        for (int i = 1; i < substrateHeaterPower.size(); i++) {
            if (substrateHeaterPower.getTimeStamp(i) > centerLayerTimeStamp) {
                startLayerTimeStamp = substrateHeaterPower.getTimeStamp(i-1);
                stopLayerTimeStamp = substrateHeaterPower.getTimeStamp(i);
            }
        }
        if (substrateHeaterPower.getValueAtTimeStamp(startLayerTimeStamp) == substrateHeaterPower.getValueAtTimeStamp(stopLayerTimeStamp)) pyrometerTemperature = Approximation.getConstant(pyrometer, startLayerTimeStamp, stopLayerTimeStamp);
        else pyrometerTemperature = Approximation.getLinearFit(pyrometer, startLayerTimeStamp, stopLayerTimeStamp).getValue(timeStamp);

        heaterPower = substrateHeaterPower.getValueAtTimeStamp(timeStamp);
        heaterTemperature = substrateHeaterTemperature.getValueAtTimeStamp(timeStamp);

    }

    /**
     * It determines dopants and their concentrations.
     */
    private void determineDopants() {
        if (isPresent(silicon)) {
            dopants.add(SILICON_DOPANT);
            dopingLevels.add(silicon.getGrowthRate(timeStamp, pyrometerTemperature));
        }
        if (isPresent(silane)) {
            dopants.add(SILICON_DOPANT);
            dopingLevels.add(silane.getGrowthRate(timeStamp, pyrometerTemperature));
        }
        if (isPresent(magnesium)) {
            dopants.add(MAGNESIUM_DOPANT);
            dopingLevels.add(magnesium.getGrowthRate(timeStamp, pyrometerTemperature));
        }
    }


}
