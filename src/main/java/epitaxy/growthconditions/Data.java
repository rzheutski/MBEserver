package epitaxy.growthconditions;

import epitaxy.growthconditions.parameters.GrowthParameter;
import epitaxy.growthconditions.parameters.EffusionCell;
import epitaxy.growthconditions.parameters.GasFlow;
import epitaxy.growthconditions.parameters.SubstrateHeat;

import java.util.ArrayList;
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

    private GrowthParameters growthParameters = new GrowthParameters();

    private List<Integer> activeParametersId = new  ArrayList<>();

    public List<Integer> getActiveParametersId() {
        return activeParametersId;
    }

    /**
     * It builds a list of growth parameters extracted from json-file.
     * @return List of all extracted growth parameters
     */
    public List<GrowthParameter> getGrowthParameters() {
        return growthParameters.getGrowthParameters();
    }


}
