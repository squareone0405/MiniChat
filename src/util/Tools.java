package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools {
	public static String getCurentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm::ss");
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		return sdf.format(date);
	}
}
