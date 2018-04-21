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

    class GrowthParameters {


       public List<EffusionCell> effusionCells;
        public List<GasFlow> gasFlows;
        public List<SubstrateHeat> substrateHeats;

        public GrowthParameters() {
            effusionCells = new ArrayList<>();
            gasFlows = new ArrayList<>();
            substrateHeats = new ArrayList<>();
        }

        public List<GrowthParameter> getGrowthParameters() {
            List<GrowthParameter> growthParameters = new ArrayList<>();
            for (EffusionCell knudsenCell : effusionCells)
                growthParameters.add(knudsenCell);
            for (GasFlow gasFlow : gasFlows)
                growthParameters.add(gasFlow);
            for (GrowthParameter growthParameter : substrateHeats)
                growthParameters.add(growthParameter);
            return growthParameters;
        }



    }

    class GrowthParametersId {
        public int Al;
        public int Ga;
        public int Si;
        public int Mg;
        public int In;
        public int N2plasma;
        public int NH3;
        public int SiH4;
        public int heaterPower;
        public int heaterTemperature;
        public int pyrometer;
    }

 //   private GrowthParameters growthParameters = new GrowthParameters();
//    private GrowthParametersId growthParametersId = new GrowthParametersId();

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
        System.out.println(gallium.getName());
        return new ArrayList<GrowthParameter>(Arrays.asList((GrowthParameter) aluminium, (GrowthParameter) gallium, (GrowthParameter) silicon, (GrowthParameter) magnesium, (GrowthParameter) indium,
                (GrowthParameter) nitrogenPlasma, (GrowthParameter) ammonia, (GrowthParameter) silane, (GrowthParameter) heaterPower, (GrowthParameter) heaterTemperature, (GrowthParameter) pyrometerTemperature));
    }


    private List<Integer> activeParametersId = new  ArrayList<>();

    public List<Integer> getActiveParametersId() {
        return activeParametersId;
    }

    /**
     * It builds a list of growth parameters extracted from json-file.
     * @return List of all extracted growth parameters
     */
/*    public List<GrowthParameter> getGrowthParameters() {
        return growthParameters.getGrowthParameters();
    }*/

   /* public int getAluminiumId() {return growthParametersId.Al;}
    public int getGalliumId() {return  growthParametersId.Ga;}
    public int getSiliconId() {return  growthParametersId.Si;}
    public int getMagnesiumId() {return  growthParametersId.Mg;}
    public int getIndiumId() {return  growthParametersId.In;}
    public int getN2plasmaId() {return  growthParametersId.N2plasma;}
    public int getAmmoniaId() {return  growthParametersId.NH3;}
    public int getSilaneId() {return  growthParametersId.SiH4;}
    public int getHeaterPowerId() {return  growthParametersId.heaterPower;}
    public int getHeaterTemperatureId() {return  growthParametersId.heaterTemperature;}
    public int getPyrometerId() {return  growthParametersId.pyrometer;}
*/
    /**
     * Returns growth parameter corresponding to the given id.
     * @param id
     * @return GrowthParameter object or null if no growth parameter with the given id
     */
/*    public GrowthParameter getGrowthParameterById(int id) {
        for (GrowthParameter growthParameter : growthParameters.getGrowthParameters())
            if (id == growthParameter.getId()) return growthParameter;
        return null;
    }*/



}
