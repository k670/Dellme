package com.playtika.maas.mapstruct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.factory.Mappers;

public class MapStructTry1 {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {

        SimpleSourceDestinationMapper mapper = Mappers.getMapper(SimpleSourceDestinationMapper.class);

        SimpleDestination simpleDestination = new SimpleDestination();
        simpleDestination.name = "name";
        simpleDestination.description = "description";
        simpleDestination.someField = 99L;

        Map<String, Object> stringObjectMap = new HashMap<String, Object>() {{
            put("name", "some name");
            put("description", "some description");
            put("someField2", 1234567L);
            put("someField", new HashMap<String, Object>() {{
                put("nestedField1", "right!");
                put("arraykey", new HashMap<String, Object>() {{
                    put("a", "b");
                }});
            }});
        }};

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectNode node = objectMapper.valueToTree(stringObjectMap);


        putOnNode("someField2/newField/arraykey/newarraykey/adnonemorekey", "/", node, "new value");
        putOnNode("someField2/newField/arraykey123/newarraykey123/adnonemorekey", "/", node, true);
        putOnNode("someField/arraykey", "/", node, simpleDestination);
        System.out.println(objectMapper.treeToValue(node, Map.class));
        JsonNode jsonNode = node.at("/someField/arraykey");
        System.out.println(jsonNode);

//     
    }

    private static void putOnNode(String fullAddress, String separator, ObjectNode objectNode, Object value) {

        List<String> list = Arrays.asList(fullAddress.split(separator));
        int lastIndex = list.size() - 1;
        List<String> address = list.stream().limit(lastIndex).collect(Collectors.toList());
        String key = list.get(lastIndex);
        ObjectNode node = findJsonNode(address, objectNode);
        node.putPOJO(key, value);

    }


    private static ObjectNode findJsonNode(List<String> address, ObjectNode objectNode) {

        JsonNode newNode = objectNode.at("/" + address.get(0));
        if (newNode == objectMapper.missingNode() || newNode == objectMapper.nullNode() || (!newNode.isContainerNode())) {
            return createJsonNodes(address, (ObjectNode) objectNode);
        }

        for (int i = 1; i < address.size(); i++) {

            JsonNode node = newNode.at("/" + address.get(i));
            if (checkNode(node,newNode)) {
                List<String> tail = address.stream().skip(i).collect(Collectors.toList());
                if (!newNode.isContainerNode()) {
                    newNode = objectMapper.createObjectNode();
                }
                return createJsonNodes(tail, (ObjectNode) newNode);
            }
            newNode = node;
        }

        return (ObjectNode) newNode;
    }

    private static boolean checkNode(JsonNode node, JsonNode parentNode){
        return node == objectMapper.missingNode() || node == objectMapper.nullNode() || (!parentNode.isContainerNode());
    }

    private static ObjectNode createJsonNodes(List<String> keys, ObjectNode objectNode) {
        ObjectNode lastNode = objectMapper.createObjectNode();
        objectNode.set(keys.get(0), lastNode);
        for (int i = 1; i < keys.size(); i++) {
            ObjectNode newObjNode = objectMapper.createObjectNode();
            lastNode.set(keys.get(i), newObjNode);
            lastNode = newObjNode;
        }
        return lastNode;
    }
}
