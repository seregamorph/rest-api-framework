package com.seregamorph.restapi.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.CharUtils;

@UtilityClass
public class ContentUtils {

    /**
     * Takes up to thresholdSize first printable characters to log request/response body. Method can be
     * convenient to print binary payloads that start with a standard signature (like PDF or ZIP file).
     *
     * @param content original payload
     * @param thresholdSize maximum characters (negative if there is no limit)
     * @return
     */
    public static String thresholdPrintableContent(String content, int thresholdSize) {
        if (content == null) {
            return null;
        }

        int length;
        if (thresholdSize < 0) {
            length = content.length();
        } else {
            length = Math.min(content.length(), thresholdSize);
        }
        val result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            char ch = content.charAt(i);
            if (isContentPrintable(ch)) {
                result.append(ch);
            } else {
                break;
            }
        }

        if (result.length() < content.length()) {
            int more = content.length() - result.length();
            result.append("...[more ").append(more).append(" chars]");
        }

        return result.toString();
    }

    private static boolean isContentPrintable(char ch) {
        return CharUtils.isAsciiPrintable(ch) || Character.isWhitespace(ch);
    }

}
