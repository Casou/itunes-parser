package com.bparent.itunes.type;

public interface XmlType<T> {
    T getValue();
    String toXml(String paddingLeft);
}
