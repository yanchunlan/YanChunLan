package util.mylibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static long convert2long(String date, String format) {
		try {
			if (date != null && !format.equals("")) {
				SimpleDateFormat sf = new SimpleDateFormat(format);
				return sf.parse(date).getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;
	}

	public static String longToMMdd(long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(new Date(longTime));
	}

	public static String longToMMddHHmmss(long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		return sdf.format(new Date(longTime));
	}
	
	public static String longToMM_dd_HHmmss(long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		return sdf.format(new Date(longTime));
	}

	public static String longToyyyy_MM_dd_HHmm(long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(longTime));
	}
	public static String longToMM_dd_HHmm(long longTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(new Date(longTime));
	}

	public static String longToyyyyMMddHHmmss(long longTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date(longTime));
	}
	public static String dateToyyyyMMddHHmm(Date dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		return sdf.format(dateTime);
	}
	
	public static String dateToMMddHHmmss(Date dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(dateTime);
	}
	
	public static String longTomm_ss(long longTime){
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		return sdf.format(new Date(longTime));
	}
	
	public static String dateToyyyy(Date dateTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(dateTime);
	}
}
