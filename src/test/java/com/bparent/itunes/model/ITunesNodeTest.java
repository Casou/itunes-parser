package com.bparent.itunes.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ITunesNodeTest {

    @Test
    void getFieldFromItunes_should_return_field_from_itunesProperty_annotation() {
        // Given
        ITunesNode node = new GeneralDict(null);

        // When
        Field field = node.getFieldFromItunes("Major Version");

        // Then
        assertNotNull(field);
        assertEquals("majorVersion", field.getName());
    }

    @Test
    void getFieldFromItunes_should_return_null_if_itunesProperty_not_found() {
        // Given
        ITunesNode node = new GeneralDict(null);

        // When
        Field field = node.getFieldFromItunes("NOT FOUND");

        // Then
        assertNull(field);
    }

}