package com.seregamorph.restapi.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.core.SpringVersion;

@UtilityClass
public class SpringVersions {

    /**
     * Checks if classpath spring version is at least target or higher.
     *
     * @param targetVersion full or major only (e.g. "5.") spring version
     * @return true is current spring version is compatible with targetVersion
     */
    public static boolean isAtLeast(String targetVersion) {
        val springVersion = SpringVersion.getVersion();
        assert springVersion != null : "Missing spring version";
        return springVersion.compareTo(targetVersion) >= 0;
    }
}
