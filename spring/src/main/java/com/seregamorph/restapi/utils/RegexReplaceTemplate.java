package com.seregamorph.restapi.utils;

import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.intellij.lang.annotations.Language;

@RequiredArgsConstructor
public class RegexReplaceTemplate {

    private final Pattern pattern;
    private final String replacement;

    /**
     * Create a regular replacement template.
     *
     * @param regex expression for pattern
     * @param replacement replacement string, may contain groups
     * @return template
     */
    public static RegexReplaceTemplate regexReplace(@Language("RegExp") String regex, String replacement) {
        return new RegexReplaceTemplate(Pattern.compile(regex), replacement);
    }

    /**
     * Replace all matched subsequences in input.
     *
     * @param input source string
     * @return updated string
     */
    public String replace(String input) {
        val matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.replaceAll(replacement);
        } else {
            return input;
        }
    }

}
