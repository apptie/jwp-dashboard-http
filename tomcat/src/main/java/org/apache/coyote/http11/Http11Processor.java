package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Container;
import org.apache.coyote.Processor;
import org.apache.coyote.context.HelloWorldContext;
import org.apache.coyote.handler.exception.InvalidQueryParameterException;
import org.apache.coyote.handler.exception.LoginFailureException;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpHeaderConsts;
import org.apache.coyote.http.util.RequestGenerator;
import org.apache.coyote.http11.exception.InvalidRequestPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final RequestGenerator REQUEST_GENERATOR = new RequestGenerator();
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<Container> contexts = new ArrayList<>();

    public Http11Processor(final Socket connection) {
        this.connection = connection;

        initContexts();
    }

    private void initContexts() {
        contexts.add(new HelloWorldContext("/"));
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            final Request request = REQUEST_GENERATOR.generate(bufferedReader);
            final Container context = findContext(request);
            final Response response = processRequest(request, context);

            bufferedWriter.write(response.convertResponseMessage());
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response processRequest(final Request request, final Container context) throws IOException {
        try {
            return context.service(request);
        } catch (final InvalidQueryParameterException e) {
            return Response.of(request, HttpStatusCode.BAD_REQUEST, ContentType.JSON, HttpConsts.BLANK);
        } catch (final LoginFailureException e) {
            return Response.of(
                    request,
                    HttpStatusCode.FOUND,
                    ContentType.JSON,
                    HttpConsts.BLANK,
                    new HeaderDto(HttpHeaderConsts.LOCATION, "/401.html")
            );
        }
    }

    private Container findContext(final Request request) {
        for (final Container context : contexts) {
            if (context.supports(request)) {
                return context;
            }
        }
        throw new InvalidRequestPathException();
    }
}
