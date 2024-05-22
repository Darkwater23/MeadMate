package com.dawsonsoftware.meadmate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate ) throws IOException {
        jsonWriter.value(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public LocalDate read( final JsonReader jsonReader ) throws IOException {
        return LocalDate.parse(jsonReader.nextString());
    }
}