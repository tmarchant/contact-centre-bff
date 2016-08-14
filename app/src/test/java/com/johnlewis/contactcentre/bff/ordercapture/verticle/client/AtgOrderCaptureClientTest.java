package com.johnlewis.contactcentre.bff.ordercapture.verticle.client;

import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
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

import static com.johnlewis.contactcentre.bff.VertxMatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(VertxUnitRunner.class)
public class AtgOrderCaptureClientTest {

    private final int MOCK_ATG_PORT = 5555;
    private final String MOCK_ATG_HOST = "localhost";

    private Vertx vertx;
    private AtgOrderCaptureApiClient verticle;
    private Router router;

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

        router = Router.router(vertx);

        Future<HttpServer> httpServerFuture = Future.future();
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(MOCK_ATG_PORT, MOCK_ATG_HOST, httpServerFuture.completer());

        httpServerFuture.setHandler(f -> async.complete());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void shouldCallRemoteAtgEndpointOnCreate(TestContext context) {
        final Async async = context.async();

        router.post("/api/rest/v1/agent/baskets")
                .handler(request -> createMockOrderCaptureCreateResponse(request));

        verticle.create().setHandler(f -> {
            JsonObject jsonResponse = new JsonObject(f.result().getData());

            assertThat(context, jsonResponse.getString("orderCaptureId"), is("a new basket ID"));
            assertThat(context, jsonResponse.getString("sessionId"), is("a new jSessionID"));

            async.complete();
        });
    }

    @Test
    public void shouldCallRemoteAtgEndpointOnGet(TestContext context) {
        final Async async = context.async();

        router.get("/api/rest/v1/agent/baskets/:basketId")
                .handler(request -> createMockOrderCaptureGetResponse(request));

        verticle.get("o1234", "mytoken").setHandler(f -> {
            RawJsonResponse getOrderCaptureResponse = f.result();
            JsonObject jsonResponse = new JsonObject(getOrderCaptureResponse.getData());

            assertThat(context, jsonResponse, is(notNullValue()));
            assertThat(context, jsonResponse.getString("basketId"), is("o1234"));

            async.complete();
        });
    }

    @Test
    public void shouldDealWith404ResponseOnGet(TestContext context) {
        final Async async = context.async();

        router.get("/api/rest/v1/agent/baskets/:basketId")
                .handler(request -> respondWithError(request, 404, "basket.not.found", "No such basket"));

        verticle.get("o1234", "mytoken").setHandler(f -> {
            RawJsonResponse getOrderCaptureResponse = f.result();
            JsonObject jsonResponse = new JsonObject(getOrderCaptureResponse.getData());

            assertThat(context, getOrderCaptureResponse.getStatusCode(), is(404));
            assertThat(context, jsonResponse.getString("code"), is("basket.not.found"));

            async.complete();
        });
    }

    private void createMockOrderCaptureCreateResponse(RoutingContext routingContext) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.put("basketId", "a new basket ID");

        routingContext.response()
                .putHeader("content-type", "application/json")
                .putHeader("Set-Cookie","JSESSIONID=a new jSessionID;")
                .end(jsonResponse.encode());
    }

    private void createMockOrderCaptureGetResponse(RoutingContext routingContext) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.put("basketId", "o1234");

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(jsonResponse.encode());
    }

    private void respondWithError(RoutingContext routingContext, int statusCode, String errorCode, String message) {
        JsonObject jsonResponse = new JsonObject();

        jsonResponse.put("code", errorCode);
        jsonResponse.put("message", message);

        routingContext.response()
                .setStatusCode(statusCode)
                .putHeader("content-type", "application/json")
                .end(jsonResponse.encode());
    }
}