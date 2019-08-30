package com.bparent.itunes.model;

import com.bparent.itunes.annotations.ItunesProperty;
import lombok.Data;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class ITunesNode {

    protected static final String ELEMENT_KEY = "key";
    protected static final String ELEMENT_KEY_TRACKS = "Tracks";
    protected static final String ELEMENT_KEY_PLAYLISTS = "Playlists";
    protected static final String ELEMENT_DICT = "dict";
    protected static final String ELEMENT_ARRAY = "array";

    protected static final String TYPE_STRING = "string";
    protected static final String TYPE_INTEGER = "integer";
    protected static final String TYPE_DATE = "date";
    protected static final String TYPE_BOOLEAN_TRUE = "true";
    protected static final String TYPE_BOOLEAN_FALSE = "false";

    private static final String DATE_TIME_FORMATTER_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_PATTERN);

    protected Node node;
    protected Map<String, Object> extraProperties;

    public ITunesNode(Node node) {
        this.node = node;
        this.extraProperties = new HashMap<>();
    }

    public void fillAttributes() {
        NamedNodeMap attributes = this.node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);

            try {
                this.setField(item.getNodeName(), item.getNodeValue());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setField(String fieldName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = this.getClass().getDeclaredField(fieldName);
        this.setField(field, fieldValue);
    }

    protected void setField(Field field, Object fieldValue) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(this, fieldValue);
        } finally {
            field.setAccessible(accessible);
        }
    }

    protected Field getFieldFromItunes(String itunesProperty) {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            ItunesProperty annotation = field.getAnnotation(ItunesProperty.class);
            if (annotation != null && annotation.value().equals(itunesProperty)) {
                return field;
            }
        }

        return null;
    }

    protected Object getChildValue(String objectType, Node child) {
        if (TYPE_BOOLEAN_FALSE.equals(objectType)) {
            return Boolean.FALSE;
        }
        if (TYPE_BOOLEAN_TRUE.equals(objectType)) {
            return Boolean.TRUE;
        }

        String objectValue = child.getChildNodes().item(0).getNodeValue();
        if (TYPE_INTEGER.equals(objectType)) {
            try {
                return new Integer(objectValue);
            } catch (NumberFormatException e) {
                return new BigInteger(objectValue);
            }
        }
        if (TYPE_DATE.equals(objectType)) {
            return LocalDateTime.parse(objectValue, DATE_TIME_FORMATTER);
        }
        return objectValue;
    }

}
