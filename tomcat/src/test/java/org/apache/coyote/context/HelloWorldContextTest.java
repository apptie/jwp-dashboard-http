package org.apache.coyote.context;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.apache.coyote.context.exception.InvalidRootContextPathException;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.QueryParameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.Url;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;
import org.junit.jupiter.api.Test;

class HelloWorldContextTest {

    @Test
    void 생성자는_유효한_rootContextPath를_전달하면_HelloWorldContext를_초기화한다() {
        final HelloWorldContext actual = new HelloWorldContext("/");

        assertThat(actual).isNotNull();
    }

    @Test
    void 생성자는_유효하지_않은_rootContextPath를_전달하면_예외가_발생한다() {
        final String invalidRootContextPath = null;

        assertThatThrownBy(() -> new HelloWorldContext(invalidRootContextPath))
                .isInstanceOf(InvalidRootContextPathException.class)
                .hasMessageContaining("Root Context Path가 유효하지 않습니다.");
    }

    @Test
    void supports_메서드는_rootContextPath에_해당하는_request면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );
        final HelloWorldContext context = new HelloWorldContext("/");

        final boolean actual = context.supports(request);

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_rootContextPath에_해당하지_않는_request면_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );
        final HelloWorldContext context = new HelloWorldContext("/hello");

        final boolean actual = context.supports(request);

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_주어진_요청을_처리하고_response를_반환한다() throws IOException {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );
        final HelloWorldContext context = new HelloWorldContext("/");

        final Response actual = context.service(request);

        assertThat(actual.convertResponseMessage()).contains("HTTP/1.1 200 OK")
                                                   .contains("Content-Type: text/html;charset=utf-8")
                                                   .contains("Content-Length: 12")
                                                   .contains("Hello World!");
    }
}
