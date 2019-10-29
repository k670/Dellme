package com.playtika.maas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class KeySpliter {

    public static void main(String[] args) {
        Map<String, Object> oldMap = new HashMap<String, Object>() {{
            put("key12.key22.key33.key41", 41);
            put("key12.key22.key33", new HashMap<String, Object>() {{
                put("key44", 44);
            }});
        }};
        System.out.println("old");
        System.out.println(oldMap);

        Map<String, Object> newMap = splitKey(oldMap);
        System.out.println("new");
        System.out.println(newMap);

    }

    private static Map<String, Object> splitKey(Map<String, Object> map) {
        Map<String, Object> resMap = new HashMap<>();
        List<KeyValueMapClass> keyValueMapList = new ArrayList<>();
        map.forEach((k, v) -> {
            String[] splitStr = k.split(Pattern.quote("."));
            int length = splitStr.length - 1;
            Map<String, Object> lastMap = resMap;
            for (int i = 0; i <= length; i++) {
                if (lastMap.containsKey(splitStr[i]) && (i < length) && lastMap.get(splitStr[i]) instanceof Map) {
                    lastMap = (Map<String, Object>) lastMap.get(splitStr[i]);
                } else {
                    if (i == length) {
                        keyValueMapList.add(new KeyValueMapClass(splitStr[i], v, lastMap));
                    } else {
                        Map<String, Object> newmap = new HashMap<>();
                        lastMap.put(splitStr[i], newmap);
                        lastMap = newmap;
                    }
                }
            }
        });
        keyValueMapList.forEach(kVMap -> {
            kVMap.map.merge(kVMap.key, kVMap.value, (a, b) -> a);
        });
        return resMap;
    }

}

class KeyValueMapClass {
    String key;
    Object value;
    Map<String, Object> map;

    KeyValueMapClass(String key, Object value, Map<String, Object> map) {
        this.key = key;
        this.map = map;
        this.value = value;
    }

}