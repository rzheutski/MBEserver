package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Converts time in UNIX-format from long(milliseconds) to a readable "dd.MM.yyyy HH:mm:ss:SSS" string and back
 */
public class Time {

    public static long strToMillis45(String strTimestamp) {
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
     * It converts timestamp in UNIX-format to string representation.
     * @param longTimeStamp UNIX time in milliseconds
     * @return string in "dd.MM.yyyy HH:mm:ss:SSS" format
     */
    public static String millisToStr(long longTimeStamp) {
        Date date = new Date(longTimeStamp);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
        String strTimestamp = formatter.format(date);
        return strTimestamp;
    }
}
