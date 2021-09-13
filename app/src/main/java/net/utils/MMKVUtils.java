package net.utils;

import com.tencent.mmkv.MMKV;

import net.entity.PDFEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MMKVUtils {

    public static void saveKeys(String key, String keyValues) {
        Set<String> keys = MMKV.defaultMMKV().decodeStringSet(key);
        if (keys == null) {
            keys = new HashSet<>();
        }
        keys.add(keyValues);
        MMKV.defaultMMKV().encode(key, keys);
    }

    public static ArrayList<PDFEntity> getAllDatas(String key) {
        ArrayList<PDFEntity> result = new ArrayList<>();
        Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(key);
        if (keySet != null) {
            for (String item : keySet) {
                PDFEntity entity = MMKV.defaultMMKV().decodeParcelable(item, PDFEntity.class);
                result.add(entity);
            }
        }
        return result;
    }
}
