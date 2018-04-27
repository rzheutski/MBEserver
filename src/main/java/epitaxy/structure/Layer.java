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

    private Material startMaterial;
    private Material stopMaterial;
    private double nominalThickness;
    private double realThickness;

    private static final int GRID_SIZE = 100; // The grid size of 100 is enough for numerical integration of growth rate.

    private static final double MOLE_FRACTION_INACCURACY = 0.01;



    public Layer(int id, long startTimeStamp, long stopTimeStamp, Data data) {
        this.layerId = id;
        this.data = data;
        this.startTimeStamp = startTimeStamp;
        this.stopTimeStamp = stopTimeStamp;
        initLayer();
    }

    /**
     * Inits main parameters of the layer.
     */
    private void initLayer() {
        long centerLayerTimeStamp = (long) (startTimeStamp + stopTimeStamp)/2;
        startMaterial = new Material(data, startTimeStamp, centerLayerTimeStamp);
        stopMaterial = new Material(data, stopTimeStamp, centerLayerTimeStamp);
        nominalThickness = 0;
        long stepTime = (long)(stopTimeStamp - startTimeStamp)/GRID_SIZE;
        long startTime = startTimeStamp;
        for (int i = 0; i < GRID_SIZE; i++) {
            double startGrowthRate = new Material(data, startTime + i*stepTime, centerLayerTimeStamp).getGrowthRate();
            double stopGrowthRate = new Material(data, startTime + (i+1)*stepTime, centerLayerTimeStamp).getGrowthRate();
            nominalThickness =+ stepTime * (startGrowthRate + stopGrowthRate)/2;
            }
    }


    /**
     * Converts parameters of the layer to a human-readable format.
     * @return String info about the layer
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(startMaterial.getFormula());
      //  s.add(Double.startMaterial.getyAlN());

        return s.toString();
    }

    public Material getStartMaterial() {
        return startMaterial;
    }

    public Material getStopMaterial() {
        return stopMaterial;
    }

}