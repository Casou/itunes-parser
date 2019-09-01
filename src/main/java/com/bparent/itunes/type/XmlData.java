package com.bparent.itunes.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class XmlData implements XmlType<String> {

    private String value;

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public String toXml(String paddingLeft) {
        return String.format("\n%s<data>\n" +
                "%s%s\n" +
                "%s</data>", paddingLeft, paddingLeft, value, paddingLeft);
    }
}
