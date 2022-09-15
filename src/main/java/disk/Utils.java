package disk;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static Date getDate(String stringDate){
        Date date;
        try {
            date = simpleDateFormat1.parse(stringDate);
        } catch (Exception e) {
            date = null;
        }
        try {
            if (date == null) {
                date = simpleDateFormat2.parse(stringDate);
            }
        } catch (Exception e) {
            date = null;
        }
        return date;
    }
}
