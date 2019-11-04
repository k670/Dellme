package com.playtika.maas.mapstruct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeSave {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        Map<String, Object> map = new HashMap<String, Object>() {{
            put("a.b.c.k4", new int[][]{{6, 7, 8, 9, 0}, {11, 22, 33, 44}});
            put("a.b.c.k1", 123L);
            put("a.b.k2", "v2");
            put("a.b.c.k3", Arrays.asList(1, 2, 3, 4, 5));
            put("a.tt", new SimpleDestination());
            put("a.tt.name", "new name");
        }};
        String JACKSON_SEPARATOR = "/";
        String ADDRESS_SEPARATOR = "\\.";

        map.forEach((address, value) -> {

            putOnNode(objectNode, JACKSON_SEPARATOR, ADDRESS_SEPARATOR, address, value);

            try {
                System.out.println(objectMapper.treeToValue(objectNode, Map.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

    }

    private static void putOnNode(ObjectNode objectNode, String JACKSON_SEPARATOR, String ADDRESS_SEPARATOR, String address, Object value) {
        List<String> addressList = Arrays.asList(address.split(ADDRESS_SEPARATOR));
        int lastIndex = addressList.size() - 1;
        String key = addressList.get(lastIndex);
        String firstAddress = addressList.get(0);
        addressList = addressList.subList(1, lastIndex);

        JsonNode existingNode = objectNode.at(JACKSON_SEPARATOR + firstAddress);
        ObjectNode lastObjectNode;
        int size = addressList.size();
        if (existingNode.getNodeType() == JsonNodeType.OBJECT) {
            lastObjectNode = (ObjectNode) existingNode;
            for (int j = 0; j < size; j++) {
                existingNode = lastObjectNode.at(JACKSON_SEPARATOR + addressList.get(j));
                if (existingNode.getNodeType() != JsonNodeType.OBJECT) {
                    lastObjectNode = createJsonNodes(addressList.subList(j, size), lastObjectNode);
                    break;
                }
                lastObjectNode = (ObjectNode) existingNode;
            }
        } else {
            lastObjectNode = objectMapper.createObjectNode();
            objectNode.set(firstAddress, lastObjectNode);
            lastObjectNode = createJsonNodes(addressList, lastObjectNode);
        }

        setOnNode(value, key, lastObjectNode);
    }

    private static ObjectNode createJsonNodes(List<String> createAddressList, ObjectNode lastObjectNode) {
        for (String adrs : createAddressList) {
            ObjectNode newObjNode = objectMapper.createObjectNode();
            lastObjectNode.set(adrs, newObjNode);
            lastObjectNode = newObjNode;
        }
        return lastObjectNode;
    }

    private static void setOnNode(Object value, String key, ObjectNode lastObjectNode) {
        JsonNode jsonNode = objectMapper.valueToTree(value);
        lastObjectNode.set(key, jsonNode);
    }

}
