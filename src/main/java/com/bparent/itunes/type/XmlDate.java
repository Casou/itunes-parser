package com.bparent.itunes.type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XmlDate implements XmlType<LocalDateTime> {

    private static final String DATE_TIME_FORMATTER_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_PATTERN);

    private LocalDateTime value;

    public XmlDate(String value) {
        this.value = LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }

    @Override
    public LocalDateTime getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public String toXml(String paddingLeft) {
        return String.format("<date>%s</date>", value.format(DATE_TIME_FORMATTER));
    }
}
