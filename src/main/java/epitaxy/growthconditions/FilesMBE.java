package epitaxy.growthconditions;

import com.google.gson.Gson;
import epitaxy.growthconditions.parameters.GrowthParameter;
import epitaxy.growthconditions.parameters.GrowthParameterWithShutter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The class contains static methods that work with files from Molecular Beam Epitaxy setup
 */
public class FilesMBE {

    /**
     * It creates a file and saves timestamps and values separated tabulation character from a GrowthParameter to the file.
     * @param filePath path to the file
     * @param growthParameter a GrowthParameter object
     */
    public static void saveValues(String filePath, GrowthParameter growthParameter) {
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            BufferedWriter writer =  Files.newBufferedWriter(Paths.get(filePath), Charset.defaultCharset());
            for (int i = 0; i < growthParameter.size(); i++) {
                StringBuilder s = new StringBuilder();
                s.append(growthParameter.getTimeStamp(i) + "\t" + growthParameter.getValue(i) + System.lineSeparator());
                writer.write(s.toString());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * It reads data from Molecular Beam Epitaxy files and returns a a completed Data object
     * @param valuesFilePath path of the file containing values vs. time
     * @param shutterFilePath path of the file containing shutter states vs. time
     * @param settingsFilePath path of the json-file containing settings
     * @return completed Data object extracted from input files
     * @throws IOException
     */
    public static Data getData(String valuesFilePath, String shutterFilePath, String settingsFilePath) throws IOException {
        return getData(valuesFilePath, shutterFilePath, settingsFilePath, 0, Long.MAX_VALUE);
    }

    /**
     * It reads data from Molecular Beam Epitaxy files within the specified time interval and returns a completed Data object
     * @param valuesFilePath path of the file containing values vs. time
     * @param shutterFilePath path of the file containing shutter states vs. time
     * @param settingsFilePath path of the json-file containing settings
     * @param startTimeStamp beginning of the analyzed time interval
     * @param stopTimeStamp ending of the analyzed time interval
     * @return completed Data object extracted from input files
     * @throws IOException
     */
    public static Data getData(String valuesFilePath, String shutterFilePath, String settingsFilePath, long startTimeStamp, long stopTimeStamp) throws IOException {
        Data data = getSettings(settingsFilePath);
        List<GrowthParameter> growthParameters = data.getGrowthParameters();
        loadValues(growthParameters, valuesFilePath, startTimeStamp, stopTimeStamp);
        loadShutters(growthParameters, shutterFilePath, startTimeStamp, stopTimeStamp);
        processGrowthParameters(growthParameters);
        return data;
    }

    /**
     * It reads settings from a json-file and returns a List of GrowthParameter objects
     * @param settingsFilePath path of the json-file containing settings
     * @return Data object extracted from settings file
     * @throws IOException
     */
    private static Data getSettings(String settingsFilePath) throws IOException {
        String jsonString = Files.lines(Paths.get(settingsFilePath)).reduce("", (s1, s2) -> s1 + s2);
        Gson g = new Gson();
        Data data = g.fromJson(jsonString, Data.class);
      //  List<GrowthParameter> growthParameters = data.getGrowthParameters();
        return data;
    }

    /**
     * It converts string representation of timestamp to UNIX-format
     * @param strTimestamp input string in "dd.MM.yyyy HH:mm:ss:SSS" format
     * @return UNIX time in milliseconds
     */
    private static long timeStringToMillis(String strTimestamp) {
        DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS", Locale.ENGLISH);
        long longTimeStamp = 0;
        try {
            longTimeStamp =  ISO_DATE_FORMAT.parse(strTimestamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return longTimeStamp;
    }

    /**
     * The method decorates Double.valueOf() and returns null instead of throwing of an exception.
     */
    private static Double doubleFromString(String str) {
        try {
            return Double.valueOf(str);
        }
        catch (NumberFormatException numExc) {
            return null;
        }
    }

    /**
     * It reads values from MBE file and saves them to GrowthParameters
     * @param growthParameters input List of GrowthParameters
     * @param valuesFilePath path of the file containing values vs. time
     * @param startTimeStamp beginning of the analyzed time interval
     * @param stopTimeStamp ending of the analyzed time interval
     * @throws IOException
     */
    private static void loadValues(List<GrowthParameter> growthParameters, String valuesFilePath, long startTimeStamp, long stopTimeStamp) throws IOException {
        Iterator<String> iterator = Files.lines(Paths.get(valuesFilePath), StandardCharsets.UTF_8).iterator();
        List<String> captions = (new ArrayList<>(Arrays.asList(iterator.next().toString().split(";"))));
        captions.remove(0);
        int parametersNumber = captions.size();
        List<Integer> parameterIndexes = new ArrayList<>(parametersNumber);
        for (int i = 0; i < parametersNumber; i++) parameterIndexes.add(null);
        for (int i = 0; i < growthParameters.size(); i++) {
                for (int j = 0; j < parametersNumber; j++) {
                    if (captions.get(j).contains(growthParameters.get(i).getName())) {
                        parameterIndexes.set(j, i);
                        break;
                    }
                }
            }
        long timeStamp = 0L;
        while (iterator.hasNext()) {
            List <String> line = new ArrayList<>(Arrays.asList(iterator.next().toString().replaceAll(";", " ; ").replaceAll(",", ".").split(";")));
            timeStamp = timeStringToMillis(line.get(0));
            if ( timeStamp > stopTimeStamp ) break;
            else if ( timeStamp > startTimeStamp)
                for (int j = 0; j < parametersNumber; j++) {
                    Integer parameterIndex = parameterIndexes.get(j);
                    if ( parameterIndex != null ) {
                        Double value = doubleFromString(line.get(j + 1));
                        GrowthParameter currentGrowthParameter = growthParameters.get(parameterIndex);
                        long lastTimeStamp = (currentGrowthParameter.size() == 0) ? 0 : currentGrowthParameter.getTimeStamp(currentGrowthParameter.size() - 1);
                        if ((value != null) && ((lastTimeStamp + currentGrowthParameter.getTimeStep_ms()) <= timeStamp)) {
                            currentGrowthParameter.addValue(timeStamp, value);
                        }
                    }
                }
        }
    }

    /**
     * It reads shutter MBE file and saves shutter events to GrowthParameters
     * @param growthParameters input List of GrowthParameters
     * @param shutterFilePath path of the file containing shutter states vs. time
     * @param startTimeStamp beginning of the analyzed time interval
     * @param stopTimeStamp ending of the analyzed time interval
     * @throws IOException
     */
    private static void loadShutters(List<GrowthParameter> growthParameters, String shutterFilePath, long startTimeStamp, long stopTimeStamp) throws IOException {
        Iterator<String> iterator = Files.lines(Paths.get(shutterFilePath), StandardCharsets.UTF_8).iterator();
        List<String> captions = (new ArrayList<>(Arrays.asList(iterator.next().toString().split(";"))));
        captions.remove(0);
        int parametersNumber = captions.size();
        List<Integer> parameterIndexes = new ArrayList<>(parametersNumber);
        for (int i = 0; i < parametersNumber; i++) parameterIndexes.add(null);
        for (int j = 0; j < growthParameters.size(); j++) {
            for (int i = 0; i < parametersNumber; i++) {
                if ((captions.get(i).contains(growthParameters.get(j).getName())) && (growthParameters.get(j) instanceof GrowthParameterWithShutter) ) {
                    parameterIndexes.set(i, j);
                    break;
                }
            }
        }
        long timeStamp = 0L;
        List<String> line;
        List<Boolean> previousLine = new ArrayList<>(parametersNumber);
        List<Boolean> currentLine = new ArrayList<>(parametersNumber);
        for (int i = 0; i < parametersNumber; i++) {
            previousLine.add(false);
            currentLine.add(false);
        }
        while (iterator.hasNext()) {
            line = new ArrayList<>(Arrays.asList(iterator.next().toString().replaceAll(";", " ; ").split(";")));
            for (int i = 0; i < parametersNumber; i++) currentLine.set(i, getShutterState(line.get(i+1)));
            timeStamp = timeStringToMillis(line.get(0));
            if (timeStamp > stopTimeStamp) break;
            if (timeStamp >= startTimeStamp) {
                for (int i = 0; i < parametersNumber; i++) {
                    if ( (!currentLine.get(i).equals(previousLine.get(i))) && (parameterIndexes.get(i) != null) && (growthParameters.get(parameterIndexes.get(i)) instanceof GrowthParameterWithShutter) )
                    {
                        GrowthParameterWithShutter grParameter = (GrowthParameterWithShutter) growthParameters.get(parameterIndexes.get(i));
                        grParameter.setShutterEvent(timeStamp, currentLine.get(i));
                        previousLine.set(i, currentLine.get(i));
                    }
                }
            }
        }
    }

    /**
     * It converts string representation of a shutter state to boolean type
     * @param s input string
     * @return boolean "true" - shutter switch on; "false" - shutter switch off
     */
    private static boolean getShutterState(String s) {
        if ( (s.trim().toLowerCase().equals("вкл")) || (s.trim().toLowerCase().equals("on")) ) return true;
        else return false;
    }

    /**
     * It makes final processing of all the growth parameters. The method can be call only after loading of values and shutters.
     * @param growthParameters
     */
    private static void processGrowthParameters(List<GrowthParameter> growthParameters) {
        for (GrowthParameter growthParameter : growthParameters)
            growthParameter.process();
    }
}
