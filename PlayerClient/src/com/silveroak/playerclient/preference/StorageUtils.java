package com.silveroak.playerclient.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.silveroak.playerclient.utils.JsonUtils;

import java.io.*;

/**
 * Created by haoquanqing on 14-6-1.
 */
public class StorageUtils {
    public static final String _CURRENT_PLAYER_LIST = "player_client_db";
    private static int _BITMAP_QUALITY = 100;

    private static StorageUtils instance = null;
    private Context m_context;

    private StorageUtils() {
    }

    public static StorageUtils getInstance(Context context) {
        if (instance == null) {
            instance = new StorageUtils();
        }
        instance.initStorageUtils(context);
        return instance;
    }

    protected void initStorageUtils(Context context) {
        m_context = context;
    }

    public void pushSettingData(String perferenceName, StorageHelperI helper) {
        if (m_context == null)
            throw new RuntimeException("StorageUtils.pushSettingData please invoking initStorageUtils first.");
        SharedPreferences.Editor editor = m_context.getSharedPreferences(perferenceName, Context.MODE_PRIVATE).edit();
        editor.putString(helper.getKey(), helper.onSaveData());
        editor.commit();
    }

    public <T> T pullSettingData(String perferenceName, String key, Class<T> clazz) {
        if (m_context == null)
            throw new RuntimeException("StorageUtils.pullSettingData please invoking initStorageUtils first.");
        SharedPreferences preferences = m_context.getSharedPreferences(perferenceName, Context.MODE_APPEND);
        String jsonValue = preferences.getString(key, "{}");
        return JsonUtils.string2Object(jsonValue, clazz);
    }

    public Bitmap pullImageData(String path) {
        BufferedInputStream in = null;
        Bitmap result = null;
        File file = new File(path);
        try {
            if (file.exists()) {
                in = new BufferedInputStream(new FileInputStream(file));
                result = BitmapFactory.decodeStream(in);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // OK to ignore
                }
            }
        }
        return result;
    }

    public void pushImageData(String path, Bitmap bitmap) {
        File file = new File(path);
        BufferedOutputStream out = null;
        try {
            file.createNewFile();
            file.deleteOnExit();
            out = new BufferedOutputStream(new FileOutputStream(file));
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, _BITMAP_QUALITY, out);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // OK to ignore
                }
            }
        }
    }

    public void clearData(String preferenceName, String key) {
        SharedPreferences cc = m_context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if (cc != null) {
            SharedPreferences.Editor edit = cc.edit();
            if (edit != null) {
                edit.remove(key);
                edit.commit();
            }
        }
    }
}
