package com.johnlewis.contactcentre.bff.ordercapture.verticle.client;

import com.johnlewis.contactcentre.bff.ordercapture.domain.CreateOrderCaptureResponse;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public class AtgOrderCaptureClientTest {

    private final int MOCK_ATG_PORT = 5555;
    private final String MOCK_ATG_HOST = "localhost";

    private Vertx vertx;
    private AtgOrderCaptureApiClient verticle;

    @Before
    public void setUp(TestContext context) throws IOException {
        final Async async = context.async();

        vertx = Vertx.vertx().exceptionHandler(context.exceptionHandler());
        verticle = new AtgOrderCaptureApiClient();

        JsonObject config = new JsonObject();
        config.put("atg.port", MOCK_ATG_PORT);
        config.put("atg.host", MOCK_ATG_HOST);

        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(verticle, options, context.asyncAssertSuccess());

        Router router = Router.router(vertx);
        router.post("/api/rest/v1/agent/baskets").handler(request -> createMockOrderCaptureResponse(request));

        Future<HttpServer> httpServerFuture = Future.future();
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(MOCK_ATG_PORT, MOCK_ATG_HOST, httpServerFuture.completer());

        httpServerFuture.setHandler(f -> async.complete());
    }

    private void createMockOrderCaptureResponse(RoutingContext routingContext) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.put("basketId", "a new basket ID");

        routingContext.response()
                .putHeader("content-type", "application/json")
                .putHeader("Set-Cookie","JSESSIONID=a new jSessionID;")
                .end(jsonResponse.encodePrettily());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void shouldCallRemoteAtgEndpoint(TestContext context) {
        final Async async = context.async();

        verticle.create().setHandler(f -> {
            CreateOrderCaptureResponse createOrderCaptureResponse = f.result();

            context.assertEquals("a new basket ID", createOrderCaptureResponse.getOrderCaptureId());
            context.assertEquals("a new jSessionID", createOrderCaptureResponse.getSessionId());

            async.complete();
        });
    }
}