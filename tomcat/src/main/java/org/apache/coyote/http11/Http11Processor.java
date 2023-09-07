package org.apache.coyote.http11;

import static org.apache.coyote.http.util.ResponseGenerator.createRedirectResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Context;
import org.apache.coyote.Processor;
import org.apache.coyote.context.exception.UnsupportedApiException;
import org.apache.coyote.handler.util.exception.ResourceNotFoundException;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.response.exception.UnsupportedAcceptException;
import org.apache.coyote.http.util.RequestGenerator;
import org.apache.coyote.http11.exception.InvalidRequestPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<Context> contexts;

    public Http11Processor(final Socket connection, final List<Context> contexts) {
        this.connection = connection;
        this.contexts = contexts;
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
            final Request request = RequestGenerator.generate(bufferedReader);
            final Context context = findContext(request);
            final Response response = processRequest(request, context);

            bufferedWriter.write(response.convertResponseMessage());
        } catch (final IOException | UncheckedServletException | InvalidRequestPathException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response processRequest(final Request request, final Context context) throws IOException {
        try {
            return context.service(request);
        } catch (final ResourceNotFoundException e) {
            return createRedirectResponse(request, "/404.html");
        } catch (final UnsupportedApiException e) {
            return Response.of(request, HttpStatusCode.NOT_FOUND, ContentType.JSON, e.getMessage());
        } catch (final UnsupportedAcceptException e) {
            return Response.of(request, HttpStatusCode.NOT_ACCEPTABLE, ContentType.JSON, e.getMessage());
        }
    }

    private Context findContext(final Request request) {
        for (final Context context : contexts) {
            if (context.supports(request)) {
                return context;
            }
        }

        throw new InvalidRequestPathException();
    }
}
