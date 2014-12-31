package com.silveroak.wifiplayer.constants;

/**
 * Created by zliu on 14/12/26.
 */
public class DateConstant {
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
