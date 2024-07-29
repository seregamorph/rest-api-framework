package com.seregamorph.restapi.utils;

import static com.seregamorph.restapi.utils.ContentUtils.thresholdPrintableContent;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContentUtilsTest {

    private static final String JSON_CONTENT = ""
            + "[ {\n"
            + "  \"id\" : 1\n"
            + "} ]";

    private static final String CSV_CONTENT = ""
            + "Team,Payment platform,Logged hours,Manual,Disputed,Overtime,Requested,Approved,Payment hours,Weekly limit,Net payment,Payment status,Payment week start date\n"
            + "Sample Team,Payoneer,0.00,0.83,1.17,0.00,0.00,0.00,0.33,40,4.50,Current,2021-02-01\n"
            + "Seregamorph Engineering,Payoneer,0.00,0.00,0.00,0.00,0.00,0.00,0.00,40,533.25,Processing,2019-09-02\n";

    private static final String PDF_CONTENT_FIRST_CHARS = ""
            + "%PDF-1.4\n"
            + "%öäüß\n";

    @Test
    public void jsonWithThresholdShouldBePrintable() {
        assertEquals(""
                + "[ {\n"
                + "  \"id\" : 1...[more 4 chars]", thresholdPrintableContent(JSON_CONTENT, 14));
    }

    @Test
    public void jsonWithoutThresholdShouldBePrintable() {
        assertEquals(JSON_CONTENT, thresholdPrintableContent(JSON_CONTENT, -1));
    }

    @Test
    public void csvShouldBePrintable() {
        assertEquals(CSV_CONTENT, thresholdPrintableContent(CSV_CONTENT, CSV_CONTENT.length()));
    }

    @Test
    public void pdfShouldBePartiallyPrintable() {
        assertEquals("%PDF-1.4\n"
                + "%...[more 5 chars]", thresholdPrintableContent(PDF_CONTENT_FIRST_CHARS, 256));
    }

}
