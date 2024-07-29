package com.seregamorph.restapi.client;

import com.seregamorph.restapi.utils.ContentUtils;
import com.seregamorph.restapi.utils.PrintStringWriter;
import com.seregamorph.restapi.utils.RegexReplaceTemplate;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Interceptor for RestTemplate/TestRestTemplate that logs HTTP request and response (on the level of RestTemplate
 * layer, e.g. it doesn't print "Host" header (added by real HTTP client)).
 */
public abstract class AbstractLoggingInterceptor<T extends AbstractLoggingInterceptor<T>>
        implements ClientHttpRequestInterceptor {

    private static final int PRINT_BODY_SIZE_THRESHOLD = 262144;

    private final List<RegexReplaceTemplate> requestReplaceTemplates = new ArrayList<>();
    private final List<RegexReplaceTemplate> responseReplaceTemplates = new ArrayList<>();

    private int printBodySizeThreshold = PRINT_BODY_SIZE_THRESHOLD;

    public T addRequestReplaceTemplate(RegexReplaceTemplate replaceTemplate) {
        requestReplaceTemplates.add(replaceTemplate);
        return self();
    }

    public T addResponseReplaceTemplate(RegexReplaceTemplate replaceTemplate) {
        responseReplaceTemplates.add(replaceTemplate);
        return self();
    }

    /**
     * Set print body size threshold (negative if there is no limit).
     */
    public T setPrintBodySizeThreshold(int printBodySizeThreshold) {
        this.printBodySizeThreshold = printBodySizeThreshold;
        return self();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] requestBody,
                                        ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = null;
        byte[] responseBody = null;
        Throwable exception = null;
        try {
            response = execution.execute(request, requestBody);
            // Implementation note: for default SimpleClientHttpRequestFactory getStatusCode() should be called
            // before getBody() for failed requests (4xx and 5xx), otherwise getBody() will throw IOException.
            HttpStatus httpStatus = response.getStatusCode();
            responseBody = StreamUtils.copyToByteArray(response.getBody());
            return getClientHttpResponse(httpStatus, response, responseBody);
        } catch (IOException | RuntimeException | Error e) {
            exception = e;
            throw e;
        } finally {
            val out = new PrintStringWriter();
            out.print(formatRequest(request, requestBody));
            try {
                if (response != null && responseBody != null) {
                    out.println();
                    out.print(formatResponse(response, responseBody));
                }
            } catch (IOException e) {
                e.printStackTrace(out);
            }

            if (exception == null) {
                logSuccess(out.toString());
            } else {
                // 4xx, 5xx, connect/socket/read timeout
                logFailure(out.toString(), exception);
            }
        }
    }

    protected abstract void logSuccess(String message);

    protected abstract void logFailure(String message, Throwable exception);

    private String formatRequest(HttpRequest request, byte[] requestBody) {
        val out = new PrintStringWriter();

        out.println("Request");
        out.println(request.getMethod() + " " + request.getURI());
        printHeaders(out, request.getHeaders());
        if (requestBody != null && requestBody.length > 0) {
            val content = getContent(request.getHeaders(), requestBody);
            val body = ContentUtils.thresholdPrintableContent(content, printBodySizeThreshold);
            out.println();
            out.println(body);
        }

        return replaceAll(out.toString(), requestReplaceTemplates);
    }

    private String formatResponse(ClientHttpResponse response, byte[] responseBody) throws IOException {
        val out = new PrintStringWriter();

        out.println("Response");
        out.println("HTTP " + response.getStatusCode().value() + " " + response.getStatusCode().getReasonPhrase());
        printHeaders(out, response.getHeaders());
        val content = getContent(response.getHeaders(), responseBody);
        val body = ContentUtils.thresholdPrintableContent(content, printBodySizeThreshold);
        out.println();
        out.println(body);
        out.println();
        out.println("Response body length " + responseBody.length + " bytes");

        return replaceAll(out.toString(), responseReplaceTemplates);
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    private static ClientHttpResponse getClientHttpResponse(HttpStatus httpStatus, ClientHttpResponse response,
                                                            byte[] responseBody) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() {
                return httpStatus;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return response.getRawStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return response.getStatusText();
            }

            @Override
            public void close() {
                response.close();
            }

            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(responseBody);
            }

            @Override
            public HttpHeaders getHeaders() {
                return response.getHeaders();
            }
        };
    }

    private static String replaceAll(String input, List<RegexReplaceTemplate> replaceTemplates) {
        String result = input;
        for (RegexReplaceTemplate replaceTemplate : replaceTemplates) {
            result = replaceTemplate.replace(result);
        }
        return result;
    }

    private static void printHeaders(PrintWriter out, Map<String, List<String>> headers) {
        headers.forEach((name, values) -> values.forEach(value -> out.println(name + ": " + value)));
    }

    private static String getContent(HttpHeaders headers, byte[] bytes) {
        return new String(bytes, getContentTypeCharset(headers));
    }

    private static Charset getContentTypeCharset(HttpHeaders headers) {
        val contentType = headers.getContentType();
        if (contentType != null) {
            val charset = contentType.getCharset();
            if (charset != null) {
                return charset;
            }
        }
        return UTF_8;
    }

}
