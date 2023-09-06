package nextstep.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.application.UserService;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginHandlerTest {

    @Test
    void 생성자는_경로와_rootContextPath를_전달하면_LoginHandler를_초기화한다() {
        final LoginHandler actual = new LoginHandler("/login", new UserService());

        assertThat(actual).isNotNull();
    }

    @Test
    void supports_메서드는_지원하는_요청인_경우_true를_반환한다() {
        final LoginHandler handler = new LoginHandler("/login", new UserService());
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: application/json");
        final RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromBodyContent("account=gugu&password=password")
        );

        final boolean actual = handler.supports(request, "/");

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_지원하지_않는_요청인_경우_false를_반환한다() {
        final LoginHandler handler = new LoginHandler("/login", new UserService());
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = handler.supports(request, "/");

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_요청을_처리하고_Response를_반환한다() throws IOException {
        final LoginHandler handler = new LoginHandler("/login", new UserService());
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: application/json");
        final RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromBodyContent("account=gugu&password=password")
        );
        request.initSessionManager(new SessionManager());

        final Response actual = handler.service(request, "ignored");

        assertThat(actual.convertResponseMessage()).contains("302 Found");
    }
}
