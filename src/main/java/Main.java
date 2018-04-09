import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.FilesMBE;
import epitaxy.growthconditions.datatreatment.Approximation;
import epitaxy.growthconditions.parameters.GrowthParameter;
import epitaxy.structure.Heterostructure;

import java.io.IOException;
import java.util.List;


/**
 * Created by DELL on 02.10.2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        Data data = FilesMBE.getData("resources/input/B052/values.csv", "resources/input/B052/shutters.csv", "resources/input/B052/settings2.json");
        Heterostructure B052 = new Heterostructure(data);

        List<GrowthParameter> growthParameters = data.getGrowthParameters();

        FilesMBE.saveValues("resources/output/pyro.txt", growthParameters.get(9));
        System.out.println("slope = " + Approximation.getLinearFit(growthParameters.get(9), 1503230089052l, 1503256991992l).getValue(1503256991992l));
        System.out.println("constant = " + Approximation.getConstant(growthParameters.get(9), 1503230089052l, 1503256991992l));



/*        for (GrowthParameter growthParameter : growthParameters)
        if (growthParameter instanceof GrowthParameterWithShutter) {
            System.out.println(((GrowthParameterWithShutter) growthParameter).ShuttterEventsToString());

            Map<Long,Long> intervals = growthParameter.getIntervals();
            StringBuilder s = new StringBuilder();
            s.append("{");
            for (Map.Entry<Long, Long> entry : intervals.entrySet()) {
                s.append(Time.millisToStr(entry.getKey())).append(" - ").append(Time.millisToStr(entry.getValue())).append(", ");
            }
            s.append("}");
            System.out.println(s);
        }*/


        System.out.println("finish");
    }
}
