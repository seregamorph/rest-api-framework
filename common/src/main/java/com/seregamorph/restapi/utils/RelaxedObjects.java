package com.seregamorph.restapi.utils;

import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelaxedObjects {

    // Implementation note: The name is inspired by Objects. Instead of Objects.equals(Object, Object), we could use
    // RelaxedObjects.equals(Object, Object).

    public boolean equals(Object first, Object second) {
        if (first instanceof Number && second instanceof Number) {
            if (first instanceof Long || second instanceof Long) {
                // Do NOT use item.longValue() here - we don't want 1.0D to equal to 1L
                try {
                    return Long.parseLong(first.toString()) == Long.parseLong(second.toString());
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            if (first instanceof Integer || second instanceof Integer) {
                // Do NOT use item.intValue() here - we don't want 1.0D to equal to 1
                try {
                    return Integer.parseInt(first.toString()) == Integer.parseInt(second.toString());
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            // Check for Float before checking for Double. This means, if 1 value is Float and the other is Double,
            // then we compare their float values. This basically takes care of loss of precision that may happen
            // in the 2 representations.

            if (first instanceof Float || second instanceof Float) {
                return ((Number) first).floatValue() == ((Number) second).floatValue();
            }

            if (first instanceof Double || second instanceof Double) {
                return ((Number) first).doubleValue() == ((Number) second).doubleValue();
            }
        }

        if (first instanceof CharSequence && second instanceof OffsetDateTime) {
            return OffsetDateTime.parse((CharSequence) first).isEqual((OffsetDateTime) second);
        }

        if (first instanceof OffsetDateTime && second instanceof CharSequence) {
            return ((OffsetDateTime) first).isEqual(OffsetDateTime.parse((CharSequence) second));
        }

        return Objects.equals(first, second);
    }
}
