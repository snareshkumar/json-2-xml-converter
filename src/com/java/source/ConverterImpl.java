package com.java.source;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ConverterImpl implements XMLJSONConverterI {

    /**
     * Read JSON Message using org.json lib
     * Generate XML File Using STAX Writer
     *
     * @param jsonFile
     * @param xmlFile
     */
    @Override
    public String convertJSONtoXML(String jsonFile, String xmlFile) {
        String status = "Failure";
        Boolean isSingleLiterals = false;
        try {
            // 1 Create document
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            FileOutputStream out = new FileOutputStream(xmlFile);
            XMLStreamWriter writer = output.createXMLStreamWriter(out);
            writer.writeStartDocument("utf-8", "1.0");

            // 2 Parse and convert JSON Object
            String content = new String(Files.readAllBytes(Paths.get(jsonFile)));

            JSONObject jsonMessage = null;
            JSONArray jsonArr = null;

            // 3 Iterate JSON Keys & Generate XML from JSON arbitrary data

            try {
                jsonMessage = new JSONObject(content);
                writer.writeStartElement("object");
                Iterator<String> keys = jsonMessage.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonMessage.get(key);
                    handleValues(key, value, writer, jsonMessage);
                }
            } catch (JSONException e) {
                try {

                    // 4 JSON Array Handling

                    jsonArr = new JSONArray(content);
                    writer.writeStartElement("array");
                    handleJSONArray(jsonArr, null, writer);
                } catch (JSONException jsonException) {

                    // 5 Single Literals Handling

                    JsonFactory factory = new JsonFactory();
                    JsonParser parser = factory.createParser(content);
                    isSingleLiterals = true;
                    boolean valid = isNumber(content.trim());
                    if (valid) {
                        createXMLElement(null, content.trim(), "number", writer);
                    } else if (Boolean.parseBoolean(content.trim())) {
                        createXMLElement(null, content.trim(), "boolean", writer);
                    } else if (content.trim() == null || content.trim().equals("null")) {
                        createXMLElement(null, content.trim(), "null", writer);
                    } else if (content instanceof java.lang.String) {
                        createXMLElement(null, content.trim(), "string", writer);
                    } else {
                        System.out.println("Nothing is found");
                    }
                }
            }

            // 6 End Document & Flush File
            if (!isSingleLiterals) {
                writer.writeEndElement();
            }
            writer.writeEndDocument();
            writer.flush();
            writer.close();
            System.out.println("Document Writer Finished");
            status = "Success";
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 6 Return Program Status
        return status;
    }

    /**
     * Handle JSON Message with corresponding data types
     *
     * @param key
     * @param value
     * @param writer
     * @param jsonMessage
     * @throws XMLStreamException
     */
    private static void handleValues(String key, Object value, XMLStreamWriter writer, JSONObject jsonMessage) throws XMLStreamException {
        if (key != null && jsonMessage != null) {
            try {
                if (jsonMessage.get(key) instanceof JSONObject) {
                    JSONObject message = (JSONObject) jsonMessage.get(key);
                    createInnerRootElement(key, value, "object", writer);
                    handleJSONObject(message, key, writer);
                    createInnerRootEndElement(writer);
                } else if (jsonMessage.get(key) instanceof JSONArray) {
                    JSONArray jsonArr = (JSONArray) jsonMessage.get(key);
                    createInnerRootElement(key, value, "array", writer);
                    handleJSONArray(jsonArr, key, writer);
                    createInnerRootEndElement(writer);
                } else if (jsonMessage.get(key) instanceof String) {
                    createXMLElement(key, value, "string", writer);
                } else if (jsonMessage.get(key) instanceof Number) {
                    createXMLElement(key, value, "number", writer);
                } else if (jsonMessage.get(key) instanceof Boolean) {
                    createXMLElement(key, value, "boolean", writer);
                } else if (jsonMessage.get(key).equals(null)) {
                    createXMLElement(key, value, "null", writer);
                } else {
                    System.out.println("Nothing is found");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (value instanceof java.lang.String) {
                createXMLElement(null, value, "string", writer);
            } else if (value instanceof java.lang.Number) {
                createXMLElement(null, value, "number", writer);
            } else if (value instanceof java.lang.Boolean) {
                createXMLElement(null, value, "boolean", writer);
            } else if (value.equals(null)) {
                createXMLElement(null, value, "null", writer);
            }
        }

    }

    /**
     * Handling JSON Object Values
     *
     * @param message
     * @param key
     * @param writer
     * @throws JSONException
     * @throws XMLStreamException
     */
    private static void handleJSONObject(JSONObject message, String key, XMLStreamWriter writer) throws JSONException, XMLStreamException {
        Iterator<String> keys = message.keys();
        while (keys.hasNext()) {
            String jsonKey = keys.next();
            Object value = message.get(jsonKey);
            handleValues(jsonKey, value, writer, message);
        }
    }

    /**
     * Handling JSON Array Values
     *
     * @param jsonArr
     * @param key
     * @param writer
     * @throws JSONException
     * @throws XMLStreamException
     */
    private static void handleJSONArray(JSONArray jsonArr, String key, XMLStreamWriter writer) throws JSONException, XMLStreamException {

        for (int i = 0; i < jsonArr.length(); i++) {
            if (jsonArr.get(i) instanceof JSONArray) {
                JSONArray innerArr = (JSONArray) jsonArr.get(i);
                createInnerRootElement(null, null, "array", writer);
                handleJSONArray(innerArr, key, writer);
                createInnerRootEndElement(writer);
            } else if (jsonArr.get(i) instanceof JSONObject) {
                JSONObject msg = (JSONObject) jsonArr.get(i);
                createInnerRootElement(key, null, "object", writer);
                handleJSONObject(msg, key, writer);
                createInnerRootEndElement(writer);
            } else {
                Object value = jsonArr.get(i);
                handleValues(null, value, writer, null);
            }
        }

    }

    /**
     * Create Inner Root Element for Objects/Arrays
     *
     * @param key
     * @param value
     * @param dataType
     * @param writer
     * @throws XMLStreamException
     */
    private static void createInnerRootElement(String key, Object value, String dataType, XMLStreamWriter writer) throws XMLStreamException {
        if (value == null) {
            writer.writeStartElement(dataType);
        }
        if (value instanceof Object) {
            writer.writeStartElement(dataType);
            writer.writeAttribute("name", key);
        }
    }

    /**
     * End Document method for inner objects
     *
     * @param writer
     */

    private static void createInnerRootEndElement(XMLStreamWriter writer) {
        try {
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create XML Element using STAX XML Stream Writer
     *
     * @param key
     * @param value
     * @param dataType
     * @param writer
     * @throws XMLStreamException
     */

    private static void createXMLElement(String key, Object value, String dataType, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(dataType);
        if (null != key) {
            writer.writeAttribute("name", key);
        }
        writer.writeCharacters(value.toString());
        writer.writeEndElement();
    }

    /**
     * Find received value is number or not.
     *
     * @param content
     * @return
     */
    private boolean isNumber(String content) {
        try {
            Integer.parseInt(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
