// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SafeJsonGetter {

    public static JsonObject getJsonObject(JsonElement element) {

        if(isNull(element) || !element.isJsonObject()) {
            return new JsonObject();
        }

        return element.getAsJsonObject();
    }

    public static double getDouble(JsonElement element) {
        if(isNull(element)) {
            return 0.0;
        }
        return Double.parseDouble(element.getAsString());
    }


    public static int getInt(JsonElement element) {
        if(isNull(element)) {
            return 0;
        }
        return Integer.parseInt(element.getAsString());
    }

    public static long getLong(JsonElement element) {
        if(isNull(element)) {
            return 0L;
        }
        return Long.parseLong(element.getAsString());
    }

    public static String getString(JsonElement element) {
        if(isNull(element)) {
            return "";
        }

        return element.getAsString();
    }

    private static boolean isNull(JsonElement element) {
        if(element == null) {
            return true;
        }

        if(element.isJsonNull()) {
            return true;
        }

        if(element.isJsonObject()) {
            return false;
        }

        if(element.getAsString() == null || element.getAsString() == "null") {
            return true;
        }

        return false;
    }
}
