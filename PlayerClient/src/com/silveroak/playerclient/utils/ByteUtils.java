package com.silveroak.playerclient.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class ByteUtils {
    private final static String TAG = "ByteUtil";
    private static ByteBuffer buffer = ByteBuffer.allocate(8);
    private static ByteBuffer int_buffer = ByteBuffer.allocate(4);


    /**
     * @param data
     * @param start
     * @param end
     * @return
     */
    public static byte[] subByte(byte[] data, int start, int end) {
        int len = data.length;
        if (start >= len || end <= start || end < 1) {
            return null;
        }
        byte[] result = new byte[(end - start)];
        for (int i = start, j = 0; i < len; i++, j++) {
            if (i < end) {
                result[j] = data[i];
            } else {
                break;
            }
        }
        return result;
    }

    /**
     * @param data
     * @param start 从start到最后， （可以为负值，负的话从后面往前数多少位）
     * @return
     */
    public static byte[] subByte(byte[] data, int start) {
        int len = data.length;
        if (start >= len) {
            return null;
        }
        if (start < 0) {
            start = len + start;
            if (start < 0) {
                return null;
            }
        }
        byte[] result = new byte[(len - start)];
        for (int i = start, j = 0; i < len; i++, j++) {
            result[j] = data[i];
        }
        return result;
    }

    /**
     * @param data
     * @param search 查询该字节，第一次出现的位置
     * @return -1表示不存在
     */
    public static int indexOf(byte[] data, byte search) {
        int len = data.length;
        for (int i = 0; i < len; i++) {
            if (data[i] == search) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 在字节中插入某个字节
     *
     * @param data
     * @param place 插入字节的位置
     * @param b     插入的字节
     * @return 插入后的字节
     */
    public static byte[] insert(byte[] data, int place, byte b) {
        int len = data.length;
        byte[] result = new byte[(len + 1)];
        for (int i = 0, j = 0; i < len; i++, j++) {
            result[j] = data[i];
            if (i == place) {
                j++;
                result[j] = b;
            }
        }
        return result;
    }

    /**
     * 在字节流中删除某个字节
     *
     * @param data
     * @param place
     * @return
     */
    public static byte[] remove(byte[] data, int place) {
        int len = data.length;
        byte[] result = new byte[(len - 1)];
        for (int i = 0, j = 0; i < len; i++, j++) {
            if (i == place) {
                i++;
            }
            if (i >= len) {
                break;
            }
            result[j] = data[i];

        }
        return result;
    }

    /**
     * 追加合并两个字节数组
     *
     * @param data
     * @param adata
     * @return
     */
    public static byte[] append(byte[] data, byte[] adata) {
        if(data==null){
            data = new byte[0];
        }
        int len = data.length;
        if(adata==null){
            return data;
        }
        int alen = adata.length;

        byte[] result = new byte[(len + alen)];

        for (int i = 0, j = 0; i < len + alen; i++) {
            if (i < len) {
                result[i] = data[i];
                continue;
            }
            result[i] = adata[j];
            j++;
        }

        return result;

    }

    public static boolean compare(byte[] as, byte[] bs) {
        boolean result = true;
        int len = as.length;
        if (len != bs.length) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (as[i] != bs[i]) {
                result = false;
                return result;
            }
        }
        return result;
    }

    public static short byte2Short(byte bin) {
        return (short) (bin & 0xFF);

    }

    public static Integer byte2Integer(byte bin) {
        return (Integer) (bin & 0xFF);

    }

    public static Integer byte2Integer(byte[] bin) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        int size = bin.length;
        if (size <= 0) {
            return 0;
        }
        if (size < 2 && size > 0) {
            return byte2Integer(bin[0]);
        }

        for (int i = 0; i < bin.length; i++) {
            n <<= 8;
            temp = bin[i] & mask;
            n |= temp;
        }
        return n;
    }
    public static byte[] intToShort(short x) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((x >>> offset) & 0xff);
        }
        return targets;
    }
    public static byte[] intToBytes(int x) {
        int_buffer = ByteBuffer.allocate(4);
        int_buffer.putInt(x);
        return  subByte(int_buffer.array(),-2);
    }

    public static byte[] longToBytes(long x) {
        buffer.clear();
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        if(bytes==null){
            return 0l;
        }
        if (bytes.length < 8) {
            byte[] mask = new byte[(8 - bytes.length)];
            bytes = append(mask, bytes);
        }
        buffer.clear();
        buffer.put(bytes, 0, 8);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static float bytesToFloat(byte[] bytes) {
        if(bytes==null){
            return 0f;
        }
        int value1 = byte2Integer(subByte(bytes, 0, 2));
        int value2 = byte2Integer(subByte(bytes, 2));
        int rv = Math.round((float) value2 / 100);
        float rvf = rv/10.0f;
        if(value1<0){
            rvf = value1-rvf;
        } else {
            rvf = rvf + value1;
        }
        return rvf;
    }

    public static byte[] floatToBytes(float value) {
        int iv = (int) value;
        byte[] values = intToBytes(iv);

        int fv = (int) ((value-iv)*1000);
        if(fv<0){
            fv=0-fv;
        }
        values = append(values,intToBytes(fv));
        return values;
    }
    public static String byte2Hext(byte bin) {
        int i = bin & 0xFF;
        return Integer.toHexString(i);
    }


    /**
     * 对象转换为字节
     *
     * @param obj
     * @return
     */
    public static byte[] obj2Bytes(Object obj) {
        byte[] result = null;

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            result = bo.toByteArray();
            bo.close();
            oo.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static <T> T bytes2Obj(byte[] data,Class<T> clazz) {
        Object obj = null;
        try {
            if(data==null){
                return null;
            }
            ByteArrayInputStream bi = new ByteArrayInputStream(data);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
            return (T)obj;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            LogUtils.warn(TAG,e.toString());
        }
        return null;
    }


    public static void printBytes(byte[] data, String type) {
        if (type == null) {
            type = "";
        }
        System.out.println();
        System.out.println(bytes2StringPtr(data,type));
        System.out.println();

    }
    public static String bytes2StringPtr(byte[] data, String type) {
        if (type == null) {
            type = "";
        }
        StringBuffer sb = new StringBuffer();
        System.out.println();
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("".equals(type) ? "0x%x," : type + ",", data[i]));
        }
        return sb.toString();

    }
    public static String bytes2String(byte[] data, String type) {
        if (type == null) {
            type = "";
        }
        StringBuffer sb = new StringBuffer();
        System.out.println();
        for (int i = 0; i < data.length; i++) {
            if("".equals(type)) {
                sb.append(String.format("%02x", data[i]));
            }else{
                sb.append(String.format(type + "%02x", data[i]));
            }
        }
        return sb.toString();

    }

    public static void printLog(byte[] data, String tag) {
        LogUtils.debug(tag, bytes2String(data,",0x"));
    }
}
