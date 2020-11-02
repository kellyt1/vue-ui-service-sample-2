package us.mn.state.health.eh.hep.dss.utils;

import org.apache.commons.lang3.StringUtils;

public class KeyIdUtils {

    public static String getBucketPK(String id) {
        return "BUCKET#" + id;
    }

    public static String getBucketSK() {
        return "#BUCKET#";
    }

    public static String getUserPK(String id) {
        return "USER#" + id;
    }

    public static String stripBucketPrefix(String id) {
        return removePrefix(id, "BUCKET#");
    }

    public static String stripUserPrefix(String id) {
        return removePrefix(id, "USER#");
    }

    private static String removePrefix(String id, String prefix) {
        return StringUtils.removeStart(id, prefix);
    }
}
