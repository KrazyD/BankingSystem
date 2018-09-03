package com.sample;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GsonDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private final DateFormat dateFormat;

    GsonDateAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateFormat.format(date));
    }

    @Override public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            try {
                Calendar calendar = Calendar.getInstance();
                long time = Long.parseLong(jsonElement.getAsString()) + TimeZone.getDefault().getRawOffset();
                calendar.setTimeInMillis(time);

                return calendar.getTime();
            } catch (Exception e) {
                return dateFormat.parse(jsonElement.getAsString());
            }
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}
