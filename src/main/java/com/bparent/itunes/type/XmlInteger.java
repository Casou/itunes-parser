package com.bparent.itunes.type;

import java.math.BigInteger;

public class XmlInteger implements XmlType<BigInteger> {

    private BigInteger value;

    public XmlInteger(String value) {
        this.value = new BigInteger(value);
    }

    @Override
    public BigInteger getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public String toXml(String paddingLeft) {
        return String.format("<integer>%s</integer>", value);
    }
}
