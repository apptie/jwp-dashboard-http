package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 생성자는_Request를_전달하면_HttpRequest를_초기화한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY
        );

        final HttpRequest actual = new HttpRequest(request);

        assertThat(actual).isNotNull();
    }

    @Test
    void getHeader_메서드는_전달한_헤더_이름의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY
        );
        final HttpRequest httpRequest = new HttpRequest(request);

        final String actual = httpRequest.getHeader("Content-Type");

        assertThat(actual).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void 파라미터가_없는_getSession_메서드는_세션이_있으면_세션을_반환한다() {
        final SessionManager sessionManager = new SessionManager();
        final HttpSession expected = new HttpSession();
        sessionManager.add(expected);
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8 \r\n Cookie: JSESSIONID=" + expected.getId());
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY,
                HttpCookie.fromSessionId(expected.getId())
        );
        request.initSessionManager(sessionManager);
        final HttpRequest httpRequest = new HttpRequest(request);

        final HttpSession actual = httpRequest.getSession();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 파라미터가_없는_getSession_메서드는_세션이_없으면_세션을_생성해_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY
        );
        request.initSessionManager(new SessionManager());
        final HttpRequest httpRequest = new HttpRequest(request);

        final HttpSession actual = httpRequest.getSession();

        assertThat(actual).isNotNull();
    }

    @Test
    void getSession_메서드에_false를_전달하면_세션이_있으면_세션을_반환한다() {
        final SessionManager sessionManager = new SessionManager();
        final HttpSession expected = new HttpSession();
        sessionManager.add(expected);
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8 \r\n Cookie: JSESSIONID=" + expected.getId());
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY,
                HttpCookie.fromSessionId(expected.getId())
        );
        request.initSessionManager(sessionManager);
        final HttpRequest httpRequest = new HttpRequest(request);

        final HttpSession actual = httpRequest.getSession(false);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSession_메서드에_false를_전달하면_세션이_없으면_null을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY
        );
        request.initSessionManager(new SessionManager());
        final HttpRequest httpRequest = new HttpRequest(request);

        final HttpSession actual = httpRequest.getSession(false);

        assertThat(actual).isNull();
    }

    @Test
    void getParameter_메서드는_전달한_파라미터_이름의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/login?user=gugu");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );
        final HttpRequest httpRequest = new HttpRequest(request);

        final String actual = httpRequest.getParameter("user");

        assertThat(actual).isEqualTo("gugu");
    }
}
