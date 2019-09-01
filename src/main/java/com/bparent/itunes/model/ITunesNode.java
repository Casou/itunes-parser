package com.bparent.itunes.model;

import com.bparent.itunes.annotations.ItunesList;
import com.bparent.itunes.annotations.ItunesProperty;
import com.bparent.itunes.exporter.XmlExportable;
import com.bparent.itunes.type.*;
import lombok.Data;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    protected static final String TYPE_DATA = "data";
    protected static final String TYPE_BOOLEAN_TRUE = "true";
    protected static final String TYPE_BOOLEAN_FALSE = "false";

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
            } catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setField(String fieldName, String fieldValue) throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, InstantiationException {
        Field field = this.getClass().getDeclaredField(fieldName);
        this.setField(field, getXmlType(field, fieldValue));
    }

    protected void setField(Field field, XmlType fieldValue) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(this, fieldValue);
        } finally {
            field.setAccessible(accessible);
        }
    }

    protected XmlType getXmlType(Field field, String fieldValue) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (XmlType) field.getType().getDeclaredConstructors()[0].newInstance(fieldValue);
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

    protected XmlType getChildValue(String objectType, Node child) {
        if (TYPE_BOOLEAN_FALSE.equals(objectType)) {
            return XmlBoolean.FALSE;
        }
        if (TYPE_BOOLEAN_TRUE.equals(objectType)) {
            return XmlBoolean.TRUE;
        }

        String objectValue = child.getChildNodes().item(0).getNodeValue();
        if (TYPE_INTEGER.equals(objectType)) {
            return new XmlInteger(objectValue);
        }
        if (TYPE_DATE.equals(objectType)) {
            return new XmlDate(objectValue);
        }
        if (TYPE_DATA.equals(objectType)) {
            return new XmlData(objectValue.trim());
        }
        return new XmlString(objectValue);
    }

    protected String propertiesToXml(String paddingLeft) {
        List<String> allProperties = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            ItunesProperty annotation = field.getAnnotation(ItunesProperty.class);
            if (annotation != null) {
                boolean accessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value == null) {
                        continue;
                    }

                    if (field.getType().equals(List.class)) {
                        ItunesList listAnnotation = field.getAnnotation(ItunesList.class);
                        if (listAnnotation == null) {
                            System.err.println("Impossible to format list " + field.getName() + " to xml without @ItunesList property");
                            continue;
                        }
                        List<XmlExportable> childNodes = (List<XmlExportable>) value;
                        if (childNodes.isEmpty()) {
                            continue;
                        }
                        allProperties.add(String.format("%s<key>%s</key>" +
                                        "\n%s<%s>\n" +
                                        "%s\n" +
                                        "%s</%s>",
                                paddingLeft,
                                annotation.value(),
                                paddingLeft,
                                listAnnotation.value(),
                                childNodes.stream().map(XmlExportable::toXml).collect(Collectors.joining("\n")),
                                paddingLeft,
                                listAnnotation.value()
                        ));
                        continue;
                    }

                    XmlType xmlTypeValue = (XmlType) value;
                    allProperties.add(String.format("%s<key>%s</key>%s",
                            paddingLeft,
                            annotation.value(),
                            xmlTypeValue.toXml(paddingLeft)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } finally {
                    field.setAccessible(accessible);
                }
            }
        }
        return String.join("\n", allProperties);
    }

}
