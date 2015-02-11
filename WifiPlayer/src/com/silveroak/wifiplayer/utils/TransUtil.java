package com.silveroak.wifiplayer.utils;


import com.silveroak.wifiplayer.constants.DateConstant;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zliu on 2014/6/22.
 */
public class TransUtil {


    public static int[] dateToYMD(String date,String format){
        int[] dateByte = new int[]{2000,01,01};
        if(date==null || format==null){
            return dateByte;
        }
        String[] ds = date.split("-");
        if(ds.length<3){
            return dateByte;
        }
        try {
            if (format.equals(DateConstant.DATE_FORMAT.YeMeD.getDisplay())) {
                dateByte[0] = Integer.valueOf(ds[0]);
                dateByte[1] = Integer.valueOf(ds[1]);
                dateByte[2] = Integer.valueOf(ds[2]);
            } else if (format.equals(DateConstant.DATE_FORMAT.MeDeY.getDisplay())) {
                dateByte[0] = Integer.valueOf(ds[2]);
                dateByte[1] = Integer.valueOf(ds[0]);
                dateByte[2] = Integer.valueOf(ds[1]);
            } else if (format.equals(DateConstant.DATE_FORMAT.DeMeY.getDisplay())) {
                dateByte[0] = Integer.valueOf(ds[2]);
                dateByte[1] = Integer.valueOf(ds[1]);
                dateByte[2] = Integer.valueOf(ds[0]);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return dateByte;
    }

    public static Date byteToDate(int[] date,int[] time){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,date[0]);
        calendar.set(Calendar.MONTH,date[1]-1);
        calendar.set(Calendar.DAY_OF_MONTH,date[2]);
        calendar.set(Calendar.HOUR_OF_DAY,time[0]);
        calendar.set(Calendar.MINUTE,time[1]);
        return calendar.getTime();
    }

    public static String dateToStr(int[] date,int format){
       return dateToStr(date,DateConstant.DATE_FORMAT.getDataformat(format).getDisplay());
    }
    public static String dateToStr(int[] date,String format){
        if(date==null|| format==null){
            return "2000-01-01";
        }
        StringBuffer sb = new StringBuffer();
        try {
            String month = "";
            String day = "";
            if(date[1]<10) {
                month = "0";
            }
            month=month+date[1];
            if(date[2]<10){
                day="0";
            }
            day=day+date[2];
            if (format.equals(DateConstant.DATE_FORMAT.YeMeD.getDisplay())) {
                sb.append(date[0]);
                sb.append("-");
                sb.append(month);
                sb.append("-");
                sb.append(day);
            } else if (format.equals(DateConstant.DATE_FORMAT.MeDeY.getDisplay())) {
                sb.append(month);
                sb.append("-");
                sb.append(day);
                sb.append("-");
                sb.append(date[0]);
            } else if (format.equals(DateConstant.DATE_FORMAT.DeMeY.getDisplay())) {
                sb.append(day);
                sb.append("-");
                sb.append(month);
                sb.append("-");
                sb.append(date[0]);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return "2000-01-01";
        }
        return sb.toString();
    }

    public static int[] timeToHM(String time,boolean is24Hour){
        int[] timeByte = new int[]{13,00};
        if(time==null){
            return timeByte;
        }
        String[] ds = time.split(":");
        if(ds.length<2){
            return timeByte;
        }
        try {
            if(is24Hour){
                timeByte[0] = Integer.valueOf(ds[0]);
                timeByte[1] = Integer.valueOf(ds[1]);
            }else{
                String min = ds[0].split(" ")[0];
                if(time.toLowerCase().contains("pm")){
                    timeByte[0] = 12+Integer.valueOf(ds[0])>11?-12:Integer.valueOf(ds[0]);
                }else{
                    timeByte[0] = Integer.valueOf(ds[0]);
                }
                timeByte[1] = Integer.valueOf(min);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return timeByte;
    }

    public static String timeToStr(int[] timeByte,boolean is24Hour){
        StringBuffer time = new StringBuffer();
        if(timeByte==null){
            if(is24Hour){
                return "00:00";
            }else{
                return "00:00 PM";
            }
        }

        try {
            String min = String.valueOf(timeByte[1]);

            if(timeByte[1]<10){
                min="0"+min;
            }
            if(is24Hour){
                if(timeByte[0]<10){
                    time.append("0");
                }
                time.append(timeByte[0]);
                time.append(":");
                time.append(min);
            }else{
                String apm=" AM";
                if(timeByte[0]>12){
                    if(timeByte[0]-12<10){
                        time.append("0");
                    }
                    time.append(timeByte[0]-12);
                    apm=" PM";
                }else{
                    if(timeByte[0]<10){
                        time.append("0");
                    }
                    time.append(timeByte[0]);
                }
                time.append(":");
                time.append(min);
                time.append(apm);
            }

        }catch (Exception ex){
            ex.printStackTrace();
            if(is24Hour){
                return "00:00";
            }else{
                return "00:00 PM";
            }
        }
        return time.toString();
    }


    public static CharSequence formFloatValue(float value){
        return  formFloatValue(value,1);
    }

    public static CharSequence formFloatValue(float value, int floatNum){
        String format = "#0";
        if(floatNum > 0)
            format += ".";
        for(int i = 0; i < floatNum; i++){
            format += "0";
        }
        DecimalFormat df = new DecimalFormat(format);
        String result = df.format(value);
        return result;
    }
    public static String formDoubleFormat(double value, int num){
        String format = "#0";
        if(num > 0)
            format += ".";
        for(int i = 0; i < num; i++){
            format += "0";
        }
        DecimalFormat df = new DecimalFormat(format);
        return df.format(value);
    }

    public static float floatFormatDotFive(float temp){
        temp = Float.parseFloat(String.valueOf(formFloatValue(temp,3)));
        float rem = temp*10%5;
        if(rem>=3){
            rem = (float)(5-rem)/10;
        }else{
            rem = (float)rem*-1/10;
        }
        return Float.parseFloat(String.valueOf(formFloatValue((rem+temp),1)));
    }

    public static int ipToInt(String ip) {
        String[] ips = ip.split("\\.");
        return (Integer.parseInt(ips[3]) << 24) + (Integer.parseInt(ips[2]) << 16)
                + (Integer.parseInt(ips[1]) << 8) + Integer.parseInt(ips[0]);
    }

    /**
     * 将整数值进行右移位操作（>>）
     * 右移24位，右移时高位补0，得到的数字即为第一段IP
     * 右移16位，右移时高位补0，得到的数字即为第二段IP
     * 右移8位，右移时高位补0，得到的数字即为第三段IP
     * 最后一段的为第四段IP
     *
     * @param i
     * @return String
     */
    public static String intToIp(int i) {
        return  (i & 0xFF)+ "."+ ((i >> 8) & 0xFF)+ "."
                + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }


    public static String sqliteEscape(String keyWord){
        keyWord = keyWord.replace(":", "/:");
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&","/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }

}
