package epitaxy.growthconditions.datatreatment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.pow;

/**
 * The class contains static methods for resampling of a tabulated data using a piecewise-linear approximation by least-squares method.
 */
public class Resample {

    static final int MAX_NUMBER_OF_ITERATIONS = 10;


    /**
     * Finds nodes of piecewise linear approximating function for the given tabulated data
     * @param timeStamps list of arguments
     * @param values list of values
     * @param resamplingError maximal allowable error (used to determine a number of intervals)
     * @return list of indexes of points selected as nodes of piecewise linear approximating function
     */
    public static List<Integer> getNodes(List<Long> timeStamps, List<Double> values, double resamplingError) {
        if (timeStamps.size() == 0) return null;
        List<Integer> resampledPoints = roughApproximation(timeStamps, values, resamplingError);
        double prevRSS = rss(timeStamps, values, resampledPoints);
        int iteration = 0;
        while (iteration <= MAX_NUMBER_OF_ITERATIONS) {
            resampledPoints = optimize(timeStamps, values, new ArrayList(resampledPoints));
            double rSS = rss(timeStamps, values, resampledPoints);
            if ( rSS/prevRSS > 0.9) break;  // if difference between RSS values is less than 10% than optimizing stops
            prevRSS = rSS;
            iteration++;
        }
        return resampledPoints;
    }

    /**
     * Makes a rough piecewise-linear approximation of the tabulated data
     * @return list of indexes of points selected as nodes of piecewise linear approximating function
     */
    private static List<Integer> roughApproximation(List<Long> timeStamps, List<Double> values, double resamplingError) {
        List<Integer> resampledPoints = new ArrayList<Integer>();
        int t = 0;
        int size =  timeStamps.size();
        for (int i = 1; i < size; i++) {
            double k = (values.get(i) - values.get(t))/(timeStamps.get(i) - timeStamps.get(t));
            double B = values.get(t) - k*timeStamps.get(t);
            if (Math.abs(k) > 0.001/60000)  // if gradient is higher then 0.001 units per minute
                for (int j = t; j < i; j++) {
                    double delta = Math.abs(values.get(j) - (k*timeStamps.get(j) + B));
                    if (delta > resamplingError) {
                        resampledPoints.add(t);
                        t = i-1;
                        break;
                    }
                }
            if ( i == size - 1) {
                resampledPoints.add(t);
                resampledPoints.add(i);
            }
        }
        return resampledPoints;
    }

    /**
     * Finds optimal positions of the resampled points by least-squares function approximation.
     * @param roughResampledPoints list of the indexes of the resampled points obtained by rough piecewise linear approximation
     * @return list of the indexes of the optimized points
     */
    private static List<Integer> optimize(List<Long> timeStamps, List<Double> values, List<Integer> roughResampledPoints) {
        List<Integer> optimized = new ArrayList<>(roughResampledPoints.size());
        optimized.add(roughResampledPoints.get(0));
        for (int i = 1; i < (roughResampledPoints.size() - 1); i++) {
            List<Integer> interval = Arrays.asList(optimized.get(i-1), roughResampledPoints.get(i), roughResampledPoints.get(i+1));
            int variedPoint = roughResampledPoints.get(i);
            int optimalPoint = variedPoint;
            double prevRssValue = rss(timeStamps, values, interval);
            double minRSS = prevRssValue;
            int count = 0;
            int z = 1;
            while (( variedPoint > interval.get(0) ) && ( variedPoint < interval.get(2)) ) {
                variedPoint += z;
                interval.set(1, variedPoint);
                double rssValue = rss(timeStamps, values, interval);
                if (rssValue > prevRssValue) count++; else count = 0;
                if (rssValue < minRSS) {
                    minRSS = rssValue;
                    optimalPoint = variedPoint;
                }
                prevRssValue = rssValue;
                if (( count == 5 ) && (z > 0)) { z = -1; count = 0; variedPoint = roughResampledPoints.get(i); }
                if (( count == 5 ) && (z < 0)) break;
            }
            optimized.add(optimalPoint);
        }
        optimized.add(roughResampledPoints.get(roughResampledPoints.size()-1));
        return optimized;
    }

    /** Calculates Residual Sum of Squares (RSS) between tabulated data (timeStamps, values) and piecewise linear function (points)
     * returns RSS value
     * */
    private static double rss(List<Long> timeStamps, List<Double> values, List<Integer> points) {

        int numberOfIntervals = points.size() - 1;
        double result = 0; // Residual Sum of Squares
        for (int i = 1; i <= numberOfIntervals; i++) {
            double k = (values.get(points.get(i)) - values.get(points.get(i-1)))/(timeStamps.get(points.get(i)) - timeStamps.get(points.get(i-1)));
            double b = values.get(points.get(i)) - k*timeStamps.get(points.get(i));
            for (int j = points.get(i-1); j < points.get(i); j++) {
                double delta = values.get(j) - (k*timeStamps.get(j) + b);
                result += pow(delta, 2);
            }
        }
        return result;
    }

}
