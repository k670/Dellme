package com.playtika.maas;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class KeySpliter {

    public static void main(String[] args) {
        Map<String, Object> oldMap = new HashMap<String, Object>() {{
            put("key12.key22.key33.key41", 41);
            put("key12.key22.key33", 33);
            put("key12.key22.key32", 32);
            put("key12.key22.key31", 31);
            put("key12.key22.key33.key44.key55", 55);
        }};

        Map<String, Object> newMap = splitKey(oldMap);
        System.out.println(newMap);

    }

    private static Map<String, Object> splitKey(Map<String, Object> map) {
        Map<String, Object> resMap = new HashMap<>();
        map.forEach((k, v) -> {
            String[] splitStr = k.split(Pattern.quote("."));
            int length = splitStr.length - 1;
            Map<String, Object> lastMap = resMap;
            for (int i = 0; i <= length; i++) {
                if (lastMap.containsKey(splitStr[i]) && (i < length) && lastMap.get(splitStr[i]) instanceof Map) {
                    lastMap = (Map<String, Object>) lastMap.get(splitStr[i]);
                } else {
                    if (i == length) {
                        lastMap.put(splitStr[i], v);
                    } else {
                        Map<String, Object> newmap = new HashMap<>();
                        lastMap.put(splitStr[i], newmap);
                        lastMap = newmap;
                    }
                }
            }
        });
        return resMap;
    }

}
