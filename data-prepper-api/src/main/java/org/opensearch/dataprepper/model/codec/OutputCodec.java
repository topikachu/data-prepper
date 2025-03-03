/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.dataprepper.model.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.dataprepper.model.event.Event;
import org.opensearch.dataprepper.model.log.JacksonLog;
import org.opensearch.dataprepper.model.sink.Sink;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface OutputCodec {

    static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * this method get called from {@link Sink} to do initial wrapping in {@link OutputStream}
     * Implementors should do initial wrapping according to the implementation
     *
     * @param outputStream  outputStream param for wrapping
     * @param event         Event to auto-generate schema
     * @param tagsTargetKey to add tags to the record to create schema
     * @throws IOException throws IOException when invalid input is received or not able to create wrapping
     */
    void start(OutputStream outputStream, Event event, String tagsTargetKey) throws IOException;

    /**
     * this method get called from {@link Sink} to write event in {@link OutputStream}
     * Implementors should do get data from event and write to the {@link OutputStream}
     *
     * @param event         event Record event
     * @param outputStream  outputStream param to hold the event data
     * @param tagsTargetKey to add tags to the record
     * @throws IOException throws IOException when not able to write data to {@link OutputStream}
     */
    void writeEvent(Event event, OutputStream outputStream, String tagsTargetKey) throws IOException;

    /**
     * this method get called from {@link Sink} to do final wrapping in {@link OutputStream}
     * Implementors should do final wrapping according to the implementation
     *
     * @param outputStream outputStream param for wrapping
     * @throws IOException throws IOException when invalid input is received or not able to create wrapping
     */
    void complete(OutputStream outputStream) throws IOException;

    /**
     * used to get extension of file
     *
     * @return String
     */
    String getExtension();

    default Event addTagsToEvent(Event event, String tagsTargetKey) throws JsonProcessingException {
        String eventJsonString = event.jsonBuilder().includeTags(tagsTargetKey).toJsonString();
        Map<String, Object> eventData = objectMapper.readValue(eventJsonString, new TypeReference<>() {
        });
        return JacksonLog.builder().withData(eventData).build();
    }
}
