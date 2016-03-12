package com.uppoint.android.pro.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.appspot.uppoint_api.uppointApi.model.LocalePair;

import java.util.List;

/**
 */
public final class LocalePairUtil {

    private LocalePairUtil() {
        // deny instantiation
    }

    public static String toJson(List<LocalePair> pairs) {
        final JsonArray array = new JsonArray();
        for (LocalePair pair : pairs) {
            final JsonObject pairObj = new JsonObject();
            pairObj.addProperty(pair.getLocale(), pair.getValue());
            array.add(pairObj);
        }

        return array.toString();
    }
}
