package com.seregamorph.restapi.demo.controllers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = "Octet stream")
@RestController
@RequestMapping(path = OctetStreamController.ENDPOINT, produces = APPLICATION_OCTET_STREAM_VALUE)
public class OctetStreamController {

    static final String ENDPOINT = "/api/octet-stream";
    static final String ENDPOINT_ZIP = "/zip";

    private static final byte[] ZIP_CONTENT = zipContent();

//    @ApiOperation("Get static zip file, this endpoint is for validating "
//            + "FrameworkIT.getOneShouldRepeatedManageProvidedHeadersAndParameters() checking binary responses")
    @GetMapping(value = ENDPOINT_ZIP)
    public byte[] getZipFile() {
        // return static content to enforce guaranteed repeated payload
        return ZIP_CONTENT;
    }

    private static byte[] zipContent() {
        String content = "test";
        val baos = new ByteArrayOutputStream();
        try (val zos = new ZipOutputStream(baos)) {
            val zipEntry = new ZipEntry("test.txt");

            zos.putNextEntry(zipEntry);
            zos.write(content.getBytes(UTF_8));
            zos.closeEntry();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return baos.toByteArray();
    }

}
