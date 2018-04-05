package epitaxy.structure;

import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.parameters.GrowthParameter;
import util.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a single semiconductor layer of a heterostructure. Within a layer all active growth parameters either constant or change linearly.
 */
public class Layer {

    private Data data;
    private long startTimeStamp;
    private long stopTimeStamp;
    private int layerId;

    private List<GrowthParameter> significantParameters = new ArrayList<>();
    private List<Double> startValues;
    private List<Double> stopValues;

    private Material startMaterial;

    public Layer(int id, long startTimeStamp, long stopTimeStamp, Data data) {
        this.layerId = id;
        this.data = data;
        this.startTimeStamp = startTimeStamp;
        this.stopTimeStamp = stopTimeStamp;
        initLayer();
    }

    private void initLayer() {
        for (GrowthParameter parameter : data.getGrowthParameters()) {
            if (parameter.getValueAtTimeStamp(startTimeStamp/2 + stopTimeStamp/2) != null) significantParameters.add(parameter);
        }

        startMaterial = new Material(data, startTimeStamp);


        startValues = significantParameters.stream().map(parameter -> parameter.getValueAtTimeStamp(startTimeStamp)).collect(Collectors.toList());
        stopValues = significantParameters.stream().map(parameter -> parameter.getValueAtTimeStamp(stopTimeStamp)).collect(Collectors.toList());
    }


    private void determineSignificantParameters() {

    }
}