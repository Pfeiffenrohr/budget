package sonstiges;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLogger {
    
    public void log(String text) {
        
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStamp = date.format(new Date());
        System.out.println (timeStamp+ " "+text);
    }

}
