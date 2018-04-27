import epitaxy.growthconditions.Data;
import epitaxy.growthconditions.FilesMBE;
import epitaxy.growthconditions.datatreatment.Approximation;
import epitaxy.growthconditions.parameters.GrowthParameter;
import epitaxy.structure.Heterostructure;
import epitaxy.structure.Layer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;


/**
 * Created by DELL on 02.10.2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        Data data = FilesMBE.getData("resources/input/B081/B081_values.csv", "resources/input/B081/B081_shutters.csv", "resources/input/B081/B081_settings.json");
        Heterostructure B081 = new Heterostructure(data);

        for (Layer layer : B081.getLayers()) {
            System.out.println(layer.getStartMaterial().getFormula());
        }


       /* List<GrowthParameter> growthParameters = data.getGrowthParameters();

        FilesMBE.saveValues("resources/output/pyro.txt", growthParameters.get(10));
        System.out.println("slope = " + Approximation.getLinearFit(growthParameters.get(10), 1503230089052l, 1503256991992l).getValue(1503256991992l));
        System.out.println("constant = " + Approximation.getConstant(growthParameters.get(10), 1503230089052l, 1503256991992l));
*/



/*        for (GrowthParameter growthParameter : growthParameters)
        if (growthParameter instanceof Precursor) {
            System.out.println(((Precursor) growthParameter).ShuttterEventsToString());

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
