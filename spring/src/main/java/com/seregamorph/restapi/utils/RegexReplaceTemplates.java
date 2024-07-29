package com.seregamorph.restapi.utils;

import static com.seregamorph.restapi.utils.RegexReplaceTemplate.regexReplace;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexReplaceTemplates {

    public static final RegexReplaceTemplate AUTHORIZATION_HEADER_MASK =
            regexReplace("(Authorization: \\w+) .*", "$1 ***");

    public static final RegexReplaceTemplate PASSWORD_QUERY_PARAM_MASK =
            regexReplace("(password)=[^&]+", "$1=***");

    public static RegexReplaceTemplate maskHeader(String header) {
        return regexReplace("(" + Pattern.quote(header) + "): .*", "$1: ***");
    }

}
