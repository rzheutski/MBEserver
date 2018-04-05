package epitaxy.growthconditions.datatreatment;

import epitaxy.growthconditions.parameters.GrowthParameter;

/**
 * This class makes linear approximation of values of a growth parameter within a specified range
 */
public class Approximation {

    public static class LinearFit {
        double intercept;
        double slope;

        public LinearFit(double slope, double intercept) {
            this.intercept = intercept;
            this.slope = slope;
        }

        public double getIntercept() {
            return intercept;
        }

        public double getSlope() {
            return slope;
        }

        public double getFitValue(long timeStamp) {
            return slope*timeStamp + intercept;
        }
    }


    /**
     * It finds parameters of line (slope and intercept) fitting of a growth parameter by least-squares function approximation within a specified range.
     * @param growthParameter input growth parameter
     * @param startTimeStamp beginning of the range
     * @param stopTimeStamp ending of the range
     * @return an object Approximation.LinearFit containing parameters of the linear fitting function
     */
    public static LinearFit getLinearFit(GrowthParameter growthParameter, long startTimeStamp, long stopTimeStamp) {
        double XiSum = 0;
        double Xi2Sum = 0;
        double XiYiSum = 0;
        double YiSum = 0;
        int n = 0;
        for (int i = 0; i < growthParameter.size(); i++) {
            long timeStamp = growthParameter.getTimeStamp(i);
            if ((timeStamp >= startTimeStamp) && (timeStamp <= stopTimeStamp)) {
                double value = growthParameter.getValue(i);
                timeStamp -= startTimeStamp;
                XiSum += timeStamp;
                Xi2Sum += timeStamp*timeStamp;
                XiYiSum += timeStamp*value;
                YiSum += value;
                n++;
            }
        }
        double slope = (n*XiYiSum - XiSum*YiSum)/(n*Xi2Sum - XiSum*XiSum);
        double intercept = (YiSum - slope*XiSum)/n - slope*startTimeStamp;
        return new LinearFit(slope, intercept);
    }

    /**
     * It makes a linear fitting with zero slope (or fit by a constant) of a growth parameter within a specified range.
     * @param growthParameter input growth parameter
     * @param startTimeStamp beginning of the range
     * @param stopTimeStamp ending of the range
     * @return value of fitting constant which is calculated as averaged value
     */
    public static double getConstant(GrowthParameter growthParameter, long startTimeStamp, long stopTimeStamp) {
        double YiSum = 0;
        int n = 0;
        for (int i = 0; i < growthParameter.size(); i++) {
            long timeStamp = growthParameter.getTimeStamp(i);
            if ((timeStamp >= startTimeStamp) && (timeStamp <= stopTimeStamp)) {
                double value = growthParameter.getValue(i);
                YiSum += value;
                n++;
            }
        }
        return YiSum/n;

    }
}
