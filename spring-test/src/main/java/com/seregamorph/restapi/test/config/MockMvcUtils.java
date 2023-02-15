package com.seregamorph.restapi.test.config;

import com.seregamorph.restapi.annotations.Compatibility;
import com.seregamorph.restapi.utils.SpringVersions;
import java.io.IOException;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@UtilityClass
public class MockMvcUtils {

    private static final MockMvcPrintingResultHandler RESULT_HANDLER = new MockMvcPrintingResultHandler();

    public static void printRequest(MockHttpServletRequest request) throws Exception {
        RESULT_HANDLER.printRequest(request);
    }

    public static void printResponse(MockHttpServletResponse response) throws Exception {
        RESULT_HANDLER.printResponse(response);
    }

    @Compatibility("MockHttpServletRequest.getContentAsString spring 5.0+")
    @Nullable
    public static String getContentAsString(MockHttpServletRequest request) throws IOException {
        if (SpringVersions.isAtLeast("5.0")) {
            // if character encoding is not set, fallback to getReader implementation
            if (request.getCharacterEncoding() != null) {
                int contentLength = request.getContentLength();
                // spring 4 does not have getContentAsString() method
                return contentLength < 0 ? null : request.getContentAsString();
            }
        }

        val reader = request.getReader();
        // may be null in spring 4
        return reader == null ? null : IOUtils.toString(reader);
    }
}
