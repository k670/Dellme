package com.playtika.maas;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Dellme {

    public static void main(String[] args) {

        String commonKey = UUID.randomUUID().toString();
        String commonNestedKey = UUID.randomUUID().toString();
        String commonKey2 = UUID.randomUUID().toString();


/*
//dont work in Java 8
      Map<String, Object> map1 = Map
                .of(
                        commonKey, 1,
                        commonKey2, Map
                                .of(
                                        UUID.randomUUID().toString(), true,
                                        commonNestedKey, "first_v"

                                )
                );
        Map<String, Object> map2 = Map
                .of(
                        commonKey, 2,
                        commonKey2, Map
                                .of(
                                        UUID.randomUUID().toString(), true,
                                        commonNestedKey, "second_v",
                                        UUID.randomUUID().toString(), Map.of(UUID.randomUUID().toString(), 42)
                                ),
                        UUID.randomUUID().toString(), 52
                );

        System.out.println(merge(map1, map2));
        */

        Map<String, Object> map1 = new HashMap<>();
        map1.put(commonKey, 1);
        map1.put(commonKey2, new HashMap<String, Object>() {{
            put(UUID.randomUUID().toString(), true);
            put(commonNestedKey, "first_v");
        }});
        Map<String, Object> map2 = new HashMap<>();
        map2.put(commonKey, 2);
        map2.put(commonKey2, new HashMap<String, Object>() {{
            put(UUID.randomUUID().toString(), true);
            put(commonNestedKey, "second_v");
            put(UUID.randomUUID().toString(), new HashMap<String, Object>() {{
                put(UUID.randomUUID().toString(), 42);
            }});
        }});

        System.out.println(merge(map1, map2));
    }

    private static Map<String, Object> merge(Map<String, Object> first, Map<String, Object> second) {
        Map<String, Object> resMap = new HashMap<>();
/*        //save all data
        //dont work when first map contain HashSet value
        first.forEach(resMap::put);
        second.forEach((k,v)->{
            if(resMap.containsKey(k)){
                Object oldValue = resMap.get(k);
                if(oldValue.getClass()== HashSet.class){
                    Set<Object> oldValueSet = (HashSet)oldValue;
                    oldValueSet.add(v);
                }else{
                    HashSet newValue = new HashSet(2);
                    Collections.addAll(newValue,oldValue,v);
                    resMap.put(k,newValue);
                }
            }else {
                resMap.put(k,v);
            }
        });*/


        first.forEach(resMap::put);
        second.forEach((k, v) -> {
            if (resMap.containsKey(k) && v instanceof Map<?, ?> && resMap.get(k) instanceof Map<?, ?>) {
                Object oldV = resMap.get(k);
                Object nextV = ((Map) v).keySet().iterator().next();
                Object nextOldV = ((Map) oldV).keySet().iterator().next();
                if (nextV instanceof String && nextOldV instanceof String) {

                    resMap.put(k, merge((Map<String, Object>) oldV, (Map<String, Object>) v));
                } else {
                    resMap.put(k, v);
                }
            } else {
                resMap.put(k, v);
            }
        });

        return resMap;
    }
}

