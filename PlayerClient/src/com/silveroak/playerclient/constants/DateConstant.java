package com.silveroak.playerclient.constants;

import java.text.SimpleDateFormat;

/**
 * Created by zliu on 14/12/26.
 */
public class DateConstant {
    public static final Long HALF_SEC = 500l;
    public static final Long ONE_SEC = 1000l;
    public static final Long ONE_MIN = 1000*60l;
    public static final Long ONE_HOUR = ONE_MIN*60l;
    public static final Long ONE_DAY = ONE_HOUR*24;

    public static final Long MCU_BASE_TIME = 946684800000l;

    public final static String BASE_TIME_STR = "GMT 2000-01-01 00:00:00";

    public final static SimpleDateFormat LOG_FILE_TIME = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//日志名称格式   sdcard 不支持冒号命名

    public enum DATE_FORMAT{
        YeMeD(0,"yyyy-MM-dd","2014-12-31"),
        MeDeY(1,"MM-dd-yyyy","12-31-2014"),
        DeMeY(2,"dd-MM-yyyy","31-12-2014");
        private int type;
        private String format;
        private String display;
        DATE_FORMAT(int type,String format,String display){
            this.type = type;
            this.format = format;
            this.display = display;
        }
        public int getType() {
            return this.type;
        }
        public String getFormat(){
            return this.format;
        }
        public String getDisplay(){
            return this.display;
        }

        public static DATE_FORMAT getDataformat(int type) {
            DATE_FORMAT[] dformats = DATE_FORMAT.values();
            for (DATE_FORMAT  df: dformats) {
                if (df.getType() == type) {
                    return df;
                }
            }
            return null;
        }
        public static DATE_FORMAT getDateformatFromFormat(String format) {
            if(format==null){
                return null;
            }
            DATE_FORMAT[] dformats = DATE_FORMAT.values();
            for (DATE_FORMAT  df: dformats) {
                if (format.equals(df.getFormat())) {
                    return df;
                }
            }
            return null;
        }
        public static DATE_FORMAT getDateformat(String display) {
            if(display==null){
                return null;
            }
            DATE_FORMAT[] dformats = DATE_FORMAT.values();
            for (DATE_FORMAT  df: dformats) {
                if (display.equals(df.getDisplay())) {
                    return df;
                }
            }
            return null;
        }
        public static String[] getDateformats() {
            return new String[]{"2014-12-31","12-31-2014","31-12-2014"};
        }
    }
}
