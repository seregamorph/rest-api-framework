package com.seregamorph.restapi.test.config;

import com.seregamorph.restapi.utils.ContentUtils;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class MockMvcPrintingResultHandler extends PrintingResultHandler {

    /**
     * Estimated to cover almost all use-cases to avoid verbose log pollution.
     * Full log can be obtained in manual test execution (while debugging).
     */
    private static final int PRINT_BODY_CHARS_THRESHOLD = 256 * 1024;

    public MockMvcPrintingResultHandler(PrintWriter writer) {
        super(new ResultValuePrinter() {
            @Override
            public void printHeading(String heading) {
                writer.println();
                writer.println(String.format("%s:", heading));
            }

            @Override
            public void printValue(String label, @Nullable Object value) {
                if (value != null && value.getClass().isArray()) {
                    value = CollectionUtils.arrayToList(value);
                }
                writer.println(String.format("%17s = %s", label, value));
            }
        });
    }

    public MockMvcPrintingResultHandler() {
        // System.out is used as default spring print() does
        this(new PrintWriter(System.out, true));
    }

    @Override
    protected void printRequest(MockHttpServletRequest request) throws Exception {
        getPrinter().printValue("HTTP Method", request.getMethod());
        getPrinter().printValue("Request URI", request.getRequestURI());
        getPrinter().printValue("Parameters", getParamsMultiValueMap(request));
        getPrinter().printValue("Headers", getRequestHeaders(request));
        val content = MockMvcUtils.getContentAsString(request);
        if (content != null) {
            val bodyToPrint = ContentUtils.thresholdPrintableContent(content, PRINT_BODY_CHARS_THRESHOLD);
            getPrinter().printValue("Request body", bodyToPrint);
            getPrinter().printValue("Request body length", request.getContentLength());
        }
        if (request instanceof MockMultipartHttpServletRequest) {
            getPrinter().printValue("Multipart files", getMultipartFiles((MockMultipartHttpServletRequest) request));
        }
    }

    @Override
    protected void printResponse(MockHttpServletResponse response) throws Exception {
        getPrinter().printValue("Status", response.getStatus());
        getPrinter().printValue("Error message", response.getErrorMessage());
        getPrinter().printValue("Headers", getResponseHeaders(response));
        getPrinter().printValue("Content type", response.getContentType());
        val content = response.getContentAsString();
        val bodyToPrint = ContentUtils.thresholdPrintableContent(content, PRINT_BODY_CHARS_THRESHOLD);
        getPrinter().printValue("Body", bodyToPrint);
        getPrinter().printValue("Body length", response.getContentAsByteArray().length);
        if (StringUtils.isNotEmpty(response.getForwardedUrl())) {
            getPrinter().printValue("Forwarded URL", response.getForwardedUrl());
        }
        if (StringUtils.isNotEmpty(response.getRedirectedUrl())) {
            getPrinter().printValue("Redirected URL", response.getRedirectedUrl());
        }
        if (ArrayUtils.isNotEmpty(response.getCookies())) {
            getPrinter().printValue("Cookies", response.getCookies());
        }
    }

    private static MultiValueMap<String, String> getMultipartFiles(MockMultipartHttpServletRequest request) {
        val map = new LinkedMultiValueMap<String, String>();
        request.getMultiFileMap().forEach((name, files) -> {
            map.put(name, files.stream()
                    .map(file -> "Size: " + file.getSize()
                            + (file.getContentType() == null ? "" : "; Content-Type: " + file.getContentType()))
                    .collect(Collectors.toList()));
        });
        return map;
    }

}
