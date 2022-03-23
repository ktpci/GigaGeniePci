package com.ktpci.beacon;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.LongSerializationPolicy;

import java.lang.reflect.Type;

public interface C {
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Boolean.class, new BooleanSerializer())
            .registerTypeAdapter(boolean.class, new BooleanSerializer())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create();

    int SECOND_MS = 1000;
    long MINUTE_MS = SECOND_MS * 60;
    long HOUR_MS = MINUTE_MS * 60;
    long DAY_MS = HOUR_MS * 24;
    long WEEK_MS = DAY_MS * 7;

    int REQUEST_TIMEOUT = 30 * SECOND_MS;

    class BooleanSerializer implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

        @Override
        public JsonElement serialize(Boolean value, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(Boolean.TRUE.equals(value) ? "Y" : "N");
        }

        @Override
        public Boolean deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            return "Y".equals(element.getAsString());
        }
    }

    enum Gender {
        MALE("M"),
        FEMALE("F"),
        ETC("E"),;

        public String value;

        Gender(String value) {
            this.value = value;
        }
    }

    enum CheckoutType {
        NORMAL(1),
        MIC_OCCUPATION_FAILURE(2),
        @Deprecated
        IOS_UNUSED(3),
        RPS_PERMISSION_NOT_GRANTED(4), // RPS : READ_PHONE_STATE
        MIC_PERMISSION_NOT_GRANTED(5),
        BLE_PERMISSION_NOT_GRANTED(6);

        public int value;

        CheckoutType(int value) {
            this.value = value;
        }
    }
}
