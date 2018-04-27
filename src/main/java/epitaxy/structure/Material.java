package epitaxy.structure;

import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.datatreatment.Approximation;
import epitaxy.growthconditions.parameters.EffusionCell;
import epitaxy.growthconditions.parameters.GasFlow;
import epitaxy.growthconditions.parameters.GrowthParameter;
import epitaxy.growthconditions.parameters.SubstrateHeat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import static epitaxy.structure.MaterialType.*;

/**
 * This class composes a nitride material and its properties using a set of growth parameters.
 */
public class Material {

    private static final String SILICON_DOPANT = "Si";
    private static final String MAGNESIUM_DOPANT = "Mg";


    private Data data;
    private List<GrowthParameter> parameters;
    private long centerLayerTimeStamp;

    private long timeStamp;
    private double pyrometerTemperature;
    private double heaterPower;
    private double heaterTemperature;

    private double xInN; // Indium nitride mole fraction
    private double yAlN; // Aluminium nitride mole fraction

    private double growthRate = 3.0;
    private MaterialType materialType;
    private boolean metalRichConditions;

    private List<String> dopants;
    private List<Double> dopingLevels;

    public Material(Data data, long timeStamp) {
        this(data, timeStamp, timeStamp);
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
        this.parameters = data.getGrowthParameters();
        initMaterial();
    }

    /**
     * Calculates all parameters of the material
     */
    private void initMaterial() {
        determineStoichiometry();
        determineDopants();
    }


    /**
     * Determines a chemical formula for the material.
     */
    private void determineStoichiometry() {
        determineSubstrateTemperature();
        if ((isPresent(data.indium) | isPresent(data.aluminium) | isPresent(data.gallium)) & (isPresent(data.ammonia) | isPresent(data.nitrogenPlasma))) InAlGaN();
        else if (isPresent(data.indium) | isPresent(data.aluminium) | isPresent(data.gallium)) metal();
        else if ((isPresent(data.silane) | isPresent(data.silicon)) & (isPresent(data.ammonia) | isPresent(data.nitrogenPlasma))) SiN();
        else if (isPresent(data.ammonia)) ammonia();
        else if (isPresent(data.nitrogenPlasma)) nitrogenPlasma();
        else empty();
    }

    /**
     * Processes an InAlGaN grown either by ammonia or by plasma molecular beam epitaxy.
     */
    private void InAlGaN() {
        materialType = InAlN;
        double desorptionFactor;
        double vNitrogen;
        double vNH3 = data.ammonia.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNplasma = data.nitrogenPlasma.getGrowthRate(timeStamp, pyrometerTemperature);
        if (vNH3 == 0) {
            // plasma MBE
            desorptionFactor = 1;
            vNitrogen = vNplasma;
        } else {
            // ammonia MBE
            desorptionFactor = 0; // no desorption
            vNitrogen = vNH3;
        }
        double vIn = data.indium.getGrowthRate(timeStamp, pyrometerTemperature, desorptionFactor);
        double vAl = data.aluminium.getGrowthRate(timeStamp, pyrometerTemperature, desorptionFactor);
        double vGa = data.gallium.getGrowthRate(timeStamp, pyrometerTemperature, desorptionFactor);
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
     * Processes a metal-deposited layer.
     */
    private void metal() {
        materialType = METAL;
        double vIn = data.indium.getGrowthRate(timeStamp, pyrometerTemperature);
        double vAl = data.aluminium.getGrowthRate(timeStamp, pyrometerTemperature);
        double vGa = data.gallium.getGrowthRate(timeStamp, pyrometerTemperature);
        growthRate = vIn + vAl + vGa;
    }

    /**
     * Processes a silicon nitride layer.
     */
    private void SiN() {
        materialType = SiN;
        double vSilicon = data.silicon.getGrowthRate(timeStamp, pyrometerTemperature);
        double vSilane = data.silane.getGrowthRate(timeStamp, pyrometerTemperature);
        double vSi = (vSilicon == 0)? vSilane : vSilicon;
        double vNH3 = data.ammonia.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNplasma = data.nitrogenPlasma.getGrowthRate(timeStamp, pyrometerTemperature);
        double vNitrogen = (vNH3 == 0)? vNplasma : vNH3;
        // A growth rate function is unknown now, therefore it is accepted as zero for the moment.
        growthRate = 0;

    }

    /**
     * Processes an imaginary layer corresponding to exposing of the sample to an ammonia flow.
     */
    private void ammonia() {
        double vNH3 = data.ammonia.getGrowthRate(timeStamp, pyrometerTemperature);
        growthRate = vNH3;
        materialType = NH3;
    }

    /**
     * Processes an imaginary layer corresponding to exposing of the sample to a nitrogen plasma flow.
     */
    private void nitrogenPlasma() {
        double vNplasma = data.nitrogenPlasma.getGrowthRate(timeStamp, pyrometerTemperature);
        growthRate = vNplasma;
        materialType = NITROGEN_PLASMA;
    }

    /**
     * Processes a case of absence of an active precursors
     */
    private void empty() {
        materialType = EMPTY;
        growthRate = 0;
    }

    /**
     * Checks if a growth parameter is actual for the given layer
     * @param parameter growth parameter to be checked
     * @return true if the growth parameter is actual or false otherwise
     */
    private boolean isPresent(GrowthParameter parameter) {
        return (parameter.getValueAtTimeStamp(centerLayerTimeStamp) != null);
    }

    /**
     * Determines pyrometer temperature at the timeStamp accepted via constructor. A pyrometer temperature is a rather specific parameter which is calculated taking into account a substrate heat power.
     */
    private void determineSubstrateTemperature() {
        long startLayerTimeStamp = 0;
        long stopLayerTimeStamp = 0;
        for (int i = 1; i < data.heaterPower.size(); i++) {
            if (data.heaterPower.getTimeStamp(i) > centerLayerTimeStamp) {
                startLayerTimeStamp = data.heaterPower.getTimeStamp(i-1);
                stopLayerTimeStamp = data.heaterPower.getTimeStamp(i);
            }
        }
        if (data.heaterPower.getValueAtTimeStamp(startLayerTimeStamp) == data.heaterPower.getValueAtTimeStamp(stopLayerTimeStamp)) pyrometerTemperature = Approximation.getConstant(data.pyrometerTemperature, startLayerTimeStamp, stopLayerTimeStamp);
        else pyrometerTemperature = Approximation.getLinearFit(data.pyrometerTemperature, startLayerTimeStamp, stopLayerTimeStamp).getValue(timeStamp);

        heaterPower = data.heaterPower.getValueAtTimeStamp(timeStamp);
     //   heaterTemperature = data.heaterTemperature.getValueAtTimeStamp(timeStamp);

    }

    /**
     * Determines dopants and their concentrations.
     */
    private void determineDopants() {
        if (isPresent(data.silicon)) {
            dopants.add(SILICON_DOPANT);
            dopingLevels.add(data.silicon.getGrowthRate(timeStamp, pyrometerTemperature));
        }
        if (isPresent(data.silane)) {
            dopants.add(SILICON_DOPANT);
            dopingLevels.add(data.silane.getGrowthRate(timeStamp, pyrometerTemperature));
        }
        if (isPresent(data.magnesium)) {
            dopants.add(MAGNESIUM_DOPANT);
            dopingLevels.add(data.magnesium.getGrowthRate(timeStamp, pyrometerTemperature));
        }
    }

    /**
     * Returns a string representation of the chemical formula for the material in a generally accepted form.
     * @returna a String representation of the chemical formula for the material
     */
    public String getFormula() {
        StringBuilder s = new StringBuilder();
        switch (materialType) {
            case InAlN:

             //   xInN = 0;
              //  yAlN = 1;
                DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
                if (xInN > 0.01) s.append("In").append(df.format(xInN));
                if (yAlN > 0.01) s.append("Al").append(df.format(yAlN));
                if ((1 - xInN - yAlN) > 0) s.append("Ga").append(df.format(1 - xInN - yAlN));
                s.append("N");
                s.append(data.aluminium.getGrowthRate(timeStamp, pyrometerTemperature, 0));
                break;
        }





        return s.toString();
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public double getPyrometerTemperature() {
        return pyrometerTemperature;
    }

    public double getHeaterPower() {
        return heaterPower;
    }

    public double getHeaterTemperature() {
        return heaterTemperature;
    }

    public double getxInN() {
        return xInN;
    }

    public double getyAlN() {
        return yAlN;
    }

    public double getGrowthRate() {
        return growthRate;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public boolean isMetalRichConditions() {
        return metalRichConditions;
    }

    public List<String> getDopants() {
        return dopants;
    }

    public List<Double> getDopingLevels() {
        return dopingLevels;
    }

}
