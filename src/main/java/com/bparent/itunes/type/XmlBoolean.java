package com.bparent.itunes.type;

public enum XmlBoolean implements XmlType<Boolean> {
    TRUE,
    FALSE;

    @Override
    public Boolean getValue() {
        return this.equals(XmlBoolean.TRUE);
    }

    @Override
    public String toString() {
        return this.equals(XmlBoolean.TRUE) + "";
    }

    @Override
    public String toXml(String paddingLeft) {
        if (this.equals(XmlBoolean.TRUE)) {
            return "<true/>";
        }
        return "<false/>";
    }
}
