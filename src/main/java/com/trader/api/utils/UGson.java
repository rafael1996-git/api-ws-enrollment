package com.trader.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;

public class UGson {

    final static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
    public static <G> List<JsonObject> toJsonObjectList(G instance) {
        return List.of((JsonObject) gson.toJsonTree(instance));
    }
}
