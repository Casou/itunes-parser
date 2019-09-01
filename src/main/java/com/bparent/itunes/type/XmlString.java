package com.bparent.itunes.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class XmlString implements XmlType<String> {

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
        return String.format("<string>%s</string>",
                value.replace("&", "&#38;")
                .replace(">", "&#62;")
        );
    }
}
