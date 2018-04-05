package epitaxy.growthconditions.parameters;

import util.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class extends the abstract GrowthParameter class and contains additional fields and methods for shutters.
 */
public abstract class GrowthParameterWithShutter extends GrowthParameter {
    protected List<Long> shutterTime;
    protected List<Boolean> shutterState; // "true" - shutter switch on; "false" - shutter switch off

    public GrowthParameterWithShutter() {
        super();
        shutterTime = new ArrayList<>();
        shutterState = new ArrayList<>();
    }

    @Override
    void determineIntervals() {
        int shutterEventsNum = shutterTime.size();
        for (int i = 0; i < shutterEventsNum; i++) {
            if (shutterState.get(i) == true) {
                List<Long> points = new ArrayList<>();
                points.add(shutterTime.get(i));
                long last = (i < (shutterEventsNum - 1)) ? shutterTime.get(i+1) : getTimeStamp(this.size() - 1);
                for (int j = 0; j < this.size(); j++)
                    if ((getTimeStamp(j) > shutterTime.get(i)) && (getTimeStamp(j) < last))
                        points.add(getTimeStamp(j));
                points.add(last);
                for (int j = 0; j < (points.size() - 1); j++) {
                    intervals.put(points.get(j), points.get(j+1));
                }
            }
        }
    }



    /**
     * This method adds an event when shutte changes its state
     * @param timeStamp timestamp of state changing
     * @param event new state of shutter ("true" - shutter switch on; "false" - shutter switch off)
     */
    public void setShutterEvent(long timeStamp, boolean event) {
        shutterTime.add(timeStamp);
        shutterState.add(event);
    }

    /**
     * This method is just for testing purposes
     * @return
     */
    public String ShuttterEventsToString() {
        StringBuilder s = new StringBuilder();
        s.append(this.getName()).append(":").append("{");
        for (int i = 0; i < shutterTime.size(); i++) {
            s.append(Time.millisToStr(shutterTime.get(i)));
            s.append(" - ").append(shutterState.get(i)).append(", ");
        }
        s.append("}");

        return s.toString();
    }

}