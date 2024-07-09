package com.microsoft.kiota.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.microsoft.kiota.serialization.mocks.TestEntity;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

class SerializationHelpersTest {
    private static final String _jsonContentType = "application/json";
    private static final String _charset = "utf-8";
    final SerializationWriter mockSerializationWriter = mock(SerializationWriter.class);
    @Test
    void defensive() {
        assertThrows(
                NullPointerException.class,
                () -> KiotaSerialization.serializeAsStream(null, (Parsable) null));
        assertThrows(
                NullPointerException.class,
                () -> KiotaSerialization.serializeAsStream(_jsonContentType, (Parsable) null));
    }

    @Test
    void defensiveCollection() {
        assertThrows(
                NullPointerException.class,
                () -> KiotaSerialization.serializeAsStream(null, (Iterable<Parsable>) null));
        assertThrows(
                NullPointerException.class,
                () ->
                        KiotaSerialization.serializeAsStream(
                                _jsonContentType, (Iterable<Parsable>) null));
    }

    @Test
    void serializesObject() throws IOException {
        when(mockSerializationWriter.getSerializedContent())
                .thenReturn(new ByteArrayInputStream("{'id':'123'}".getBytes(_charset)));
        final var mockSerializationWriterFactory = mock(SerializationWriterFactory.class);
        when(mockSerializationWriterFactory.getSerializationWriter(_jsonContentType))
                .thenReturn(mockSerializationWriter);
        SerializationWriterFactoryRegistry.defaultInstance.contentTypeAssociatedFactories.put(
                _jsonContentType, mockSerializationWriterFactory);
        final var result =
                KiotaSerialization.serializeAsString(
                        _jsonContentType,
                        new TestEntity() {
                            {
                                setId("123");
                            }
                        });
        assertEquals("{'id':'123'}", result);
        verify(mockSerializationWriter, times(1)).writeObjectValue(eq(""), any(Parsable.class));
    }

    @Test
    void serializesObjectCollection() throws IOException {
        when(mockSerializationWriter.getSerializedContent())
                .thenReturn(new ByteArrayInputStream("[{'id':'123'}]".getBytes(_charset)));
        final var mockSerializationWriterFactory = mock(SerializationWriterFactory.class);
        when(mockSerializationWriterFactory.getSerializationWriter(_jsonContentType))
                .thenReturn(mockSerializationWriter);
        SerializationWriterFactoryRegistry.defaultInstance.contentTypeAssociatedFactories.put(
                _jsonContentType, mockSerializationWriterFactory);
        final var result =
                KiotaSerialization.serializeAsString(
                        _jsonContentType,
                        new ArrayList<>() {
                            {
                                add(
                                        new TestEntity() {
                                            {
                                                setId("123");
                                            }
                                        });
                            }
                        });
        assertEquals("[{'id':'123'}]", result);
        verify(mockSerializationWriter, times(1)).writeCollectionOfObjectValues(eq(""), any());
    }
}
