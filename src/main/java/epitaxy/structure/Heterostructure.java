package epitaxy.structure;

import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.parameters.GrowthParameter;

import java.util.*;

/**
 * This class represents a whole heterostructure containing a sequence of semiconductor layers starting from substrate.
 */
public class Heterostructure {



    private List<Layer> layers;
    Data data;

    public Heterostructure(Data data) {
        this.data = data;
        initHeterostructure();
    }

    /**
     * It determines timestamps corresponding to beginning and ending of every layer and builds the heterostructure, i.e. creates a List of layers.
     */
    private void initHeterostructure() {
        layers = new ArrayList<>();
        Set<Long> layerBounds = new TreeSet<>();
     //   List<GrowthParameter> activeParameters = new ArrayList<>();
        List<GrowthParameter> growthParameters = data.getGrowthParameters();
        List<Integer> activeParametersId = data.activeParametersId;

        for (GrowthParameter growthParameter : growthParameters) {
            if (activeParametersId.contains(growthParameter.getId())) {
                Map<Long, Long> intervals = growthParameter.getIntervals();
                if (intervals != null) {
                    layerBounds.addAll(intervals.keySet());
                    layerBounds.addAll(intervals.values());
                }
            }
        }




     /*   for (int id : activeParametersId) {
            for (GrowthParameter growthParameter : growthParameters) {
                if (id == growthParameter.getId()) {
                    activeParameters.add(growthParameter);
                    break;
                }
            }
        }
        for (GrowthParameter parameter : activeParameters) {
            Map<Long, Long> intervals = parameter.getIntervals();
            if (intervals != null) {
                layerBounds.addAll(intervals.keySet());
                layerBounds.addAll(intervals.values());
            }
        }
*/
        Long [] layerBoundsArray = layerBounds.toArray(new Long[layerBounds.size()]);
        for (int i = 0; i < (layerBoundsArray.length - 1); i++) {
            layers.add(new Layer(i, layerBoundsArray[i], layerBoundsArray[i + 1], data));
        }

    }

    public List<Layer> getLayers() {
        return layers;
    }
}
