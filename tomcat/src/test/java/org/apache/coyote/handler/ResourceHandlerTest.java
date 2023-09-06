package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.handler.ResourceHandler;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResourceHandlerTest {

    @Test
    void 생성자는_static_resource의_경로를_전달하면_CssHandler를_초기화한다() {
        final ResourceHandler actual = new ResourceHandler();

        assertThat(actual).isNotNull();
    }

    @Test
    void supports_메서드는_지원하는_요청인_경우_true를_반환한다() {
        final ResourceHandler handler = new ResourceHandler();
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = handler.supports(request, "ignored");

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_지원하지_않는_요청인_경우_false를_반환한다() {
        final ResourceHandler handler = new ResourceHandler();
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = handler.supports(request, "ignored");

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_요청을_처리하고_Response를_반환한다() throws IOException {
        final ResourceHandler handler = new ResourceHandler();
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final Response actual = handler.service(request, "static/");

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.convertResponseMessage()).contains("<!DOCTYPE html>");
        });
    }
}
