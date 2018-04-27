package epitaxy.growthconditions;

import epitaxy.growthconditions.parameters.GrowthParameter;
import epitaxy.growthconditions.parameters.EffusionCell;
import epitaxy.growthconditions.parameters.GasFlow;
import epitaxy.growthconditions.parameters.SubstrateHeat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 The class serves as a container for all input data extracted from files including growth parameters, calibration constant and so on.
 */
public class Data {

    public EffusionCell aluminium;
    public EffusionCell gallium;
    public EffusionCell silicon;
    public EffusionCell magnesium;
    public EffusionCell indium;

    public GasFlow nitrogenPlasma;
    public GasFlow ammonia;
    public GasFlow silane;

    public SubstrateHeat heaterPower;
    public SubstrateHeat heaterTemperature;
    public SubstrateHeat pyrometerTemperature;


    public List<GrowthParameter> getGrowthParameters() {
     //   System.out.println(gallium.getName());
        return new ArrayList<GrowthParameter>(Arrays.asList((GrowthParameter) aluminium, (GrowthParameter) gallium, (GrowthParameter) silicon, (GrowthParameter) magnesium, (GrowthParameter) indium,
                (GrowthParameter) nitrogenPlasma, (GrowthParameter) ammonia, (GrowthParameter) silane, (GrowthParameter) heaterPower, (GrowthParameter) heaterTemperature, (GrowthParameter) pyrometerTemperature));
    }


    public List<Integer> activeParametersId = new  ArrayList<>();




}
