package com.seregamorph.restapi.utils;

import static com.seregamorph.restapi.utils.RegexReplaceTemplates.AUTHORIZATION_HEADER_MASK;
import static com.seregamorph.restapi.utils.RegexReplaceTemplates.PASSWORD_QUERY_PARAM_MASK;
import static com.seregamorph.restapi.utils.RegexReplaceTemplates.maskHeader;
import static org.junit.Assert.assertEquals;

import lombok.val;
import org.junit.Test;

public class RegexReplaceTemplateTest {

    @Test
    public void requestLogAuthorizationShouldBeCleanedUp() {
        val rawRequestLog = "GET http://localhost:59517/api/v3/vcs-repositories/github.com?search=defaultBranch%3D%22master%22\n"
                + "Accept: application/octet-stream, application/json, application/*+json, */*\n"
                + "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE1Mjg3NTYyMjYsImV4cCI6MTU2MDI5MjIxMiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidXNlckB1cmVhLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2NrZXQiLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.lYnZCtGslpU-eYD2cAgZo4hYOfREh0tGlwb_FFgdheM\n"
                + "From: someone@address.com\n"
                + "Content-Length: 0\n"
                + "X-Test-ClassName: com.seregamorph.csvcs.api.v3.controllers.VcsRepositoryRestControllerWebIT\n"
                + "X-Test-MethodName: getAllShouldManageSearchFields[/github.com;defaultBranch=\"master\"]\n"
                + "X-Test-MethodGroup: get\n"
                + "X-Test-TargetMethodGroup: /{vcsProvider}\n"
                + "X-Test-ExecutionId: /github.com;defaultBranch=\"master\"\n";

        val cleanedRequestLog = AUTHORIZATION_HEADER_MASK.replace(rawRequestLog);

        assertEquals("GET http://localhost:59517/api/v3/vcs-repositories/github.com?search=defaultBranch%3D%22master%22\n"
                + "Accept: application/octet-stream, application/json, application/*+json, */*\n"
                + "Authorization: Bearer ***\n"
                + "From: someone@address.com\n"
                + "Content-Length: 0\n"
                + "X-Test-ClassName: com.seregamorph.csvcs.api.v3.controllers.VcsRepositoryRestControllerWebIT\n"
                + "X-Test-MethodName: getAllShouldManageSearchFields[/github.com;defaultBranch=\"master\"]\n"
                + "X-Test-MethodGroup: get\n"
                + "X-Test-TargetMethodGroup: /{vcsProvider}\n"
                + "X-Test-ExecutionId: /github.com;defaultBranch=\"master\"\n", cleanedRequestLog);
    }

    @Test
    public void urlEncodedFormRequestShouldBeCleanedUp1() {
        val rawRequestLog = "POST /auth\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Content-Length: 33\n"
                + "\n"
                + "login=admin&password=Qwerty123456&mode=default";

        val cleanedRequestLog = PASSWORD_QUERY_PARAM_MASK.replace(rawRequestLog);

        assertEquals("POST /auth\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Content-Length: 33\n"
                + "\n"
                + "login=admin&password=***&mode=default", cleanedRequestLog);
    }

    @Test
    public void urlEncodedFormRequestShouldBeCleanedUp2() {
        val rawRequestLog = "POST /auth\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Content-Length: 33\n"
                + "\n"
                + "login=admin&password=Qwerty123456";

        val cleanedRequestLog = PASSWORD_QUERY_PARAM_MASK.replace(rawRequestLog);

        assertEquals("POST /auth\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Content-Length: 33\n"
                + "\n"
                + "login=admin&password=***", cleanedRequestLog);
    }

    @Test
    public void customHeaderShouldBeCleanedUp() {
        val rawRequestLog = "GET /users\n"
                + "X-Auth-Token: 123457:1610808030062:0a0a0a0a17925df689c42dd800000000\n"
                + "Accept: application/json\n"
                + "\n";

        val cleanedRequestLog = maskHeader("X-Auth-Token")
                .replace(rawRequestLog);

        assertEquals("GET /users\n"
                + "X-Auth-Token: ***\n"
                + "Accept: application/json\n"
                + "\n", cleanedRequestLog);
    }

}
