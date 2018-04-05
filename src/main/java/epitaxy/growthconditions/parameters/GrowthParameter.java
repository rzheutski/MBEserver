package epitaxy.growthconditions.parameters;

import epitaxy.growthconditions.datatreatment.Resample;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This abstract class contains calibration constants, data and settings for a growth parameters like flow or temperature
 */
public abstract class GrowthParameter {

    private int id;
    private String name;
    private String description;
    private long timeStep_ms;
    private double resamplingError;

    private List<Long> timeStamps;
    private List<Double> values;

    protected TreeMap<Long, Long> intervals;

    public GrowthParameter() {
        timeStamps = new ArrayList<>();
        values = new ArrayList<>();
        intervals = new TreeMap<>();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public long getTimeStep_ms() { return timeStep_ms; }
    public List<Long> getTimeStamps() {
        return timeStamps;
    }

    public List<Double> getValues() {
        return values;
    }

    public void addValue(long timeStamp, double value) {
        timeStamps.add(timeStamp);
        values.add(value);
    }

    public int size() {
        return timeStamps.size();
    }
    public long getTimeStamp(int index) {
        return timeStamps.get(index);
    }
    public double getValue(int index) {
        return values.get(index);
    }


    /**
     * It processes the GrowthParameter that includes resampling of its values by a piecewise linear approximation and determination of active intervals
     */
    public void process() {
        resample();
        determineIntervals();
    }

    /**
     * Makes a resampling of the values using a piecewise linear approximation by least-squares method
     */
    private void resample() {
        List<Integer> nodes =  Resample.getNodes(timeStamps, values, resamplingError);
        if ( nodes == null ) return;
        List<Long> resampledTimeStamps = new ArrayList<>(nodes.size());
        List<Double> resampledValues = new ArrayList<>(nodes.size());
        for (int node : nodes) {
            resampledTimeStamps.add(timeStamps.get(node));
            resampledValues.add(values.get(node));
        }
        timeStamps = resampledTimeStamps;
        values = resampledValues;
    }

    /**
     * It determines active intervals. Within an active interval, the value is either constant or change linearly. If the growth parameters has a shutter, an interval can be only active if the shutter is opened (i.e. "true").
     */
    void determineIntervals() {
        for (int i = 0; i < (timeStamps.size() - 1); i++) {
            intervals.put(timeStamps.get(i), timeStamps.get(i+1));
        }
    }

    /**
     * It return active intervals for the growth parameters in the form of a TreeMap. Each <Long, Long> pair in the map contains a start timestamp and an end timestamp of an interval as a key and a value respectively.
     * @return
     */
    public TreeMap<Long, Long> getIntervals() {
        return intervals;
    }

    /**
     * It returns Value taken at a certain timestamp. If the timestamp is out of the intervals, the function returns null.
     * @param timeStamp
     * @return Double Value or null.
     */
    public Double getValueAtTimeStamp(long timeStamp) {
        for (Map.Entry<Long, Long> interval : intervals.entrySet()) {
            if ((timeStamp >= interval.getKey()) && (timeStamp <= interval.getValue())) {
                int startIndex = 0;
                int stopIndex = this.size() - 1;
                while ((stopIndex - startIndex) > 1) {
                    int half = (int) (startIndex + stopIndex)/2;
                    long timeStampAtHalf = timeStamps.get(half);
                    if (timeStamp <= timeStampAtHalf) stopIndex = half;
                    else if (timeStamp > timeStampAtHalf) startIndex = half;
                }
                long startTimeStamp = getTimeStamp(startIndex);
                long stopTimeStamp = getTimeStamp(stopIndex);
                double startValue = getValue(startIndex);
                double stopValue = getValue(stopIndex);
                return (startValue + (stopValue - startValue)*(timeStamp - startTimeStamp)/(stopTimeStamp - startTimeStamp));
            }
        }
        return null;
    }




}
