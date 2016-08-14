package com.johnlewis.contactcentre.bff.ordercapture.verticle;

import com.johnlewis.contactcentre.bff.global.domain.ErrorResponse;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.domain.CreateOrderCaptureResponse;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.client.StubOrderCaptureApiClient;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URLEncoder;

import static com.johnlewis.contactcentre.bff.VertxMatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(VertxUnitRunner.class)
public class OrderCaptureVerticleTest {

    private final int PORT = 3001;

    private Vertx vertx;
    private OrderCaptureVerticle verticle;
    private StubOrderCaptureApiClient orderCaptureClient;

    @Before
    public void setUp(TestContext context) throws IOException {
        final Async async = context.async();

        vertx = Vertx.vertx().exceptionHandler(context.exceptionHandler());
        Router router = Router.router(vertx);

        orderCaptureClient = new StubOrderCaptureApiClient();
        verticle = new OrderCaptureVerticle(router, orderCaptureClient);

        vertx.deployVerticle(verticle, context.asyncAssertSuccess());

        Future<HttpServer> httpServerFuture = Future.future();
        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(PORT, "localhost", httpServerFuture.completer());

        httpServerFuture.setHandler(f -> async.complete());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void createOrderCaptureReturnsExpectedResponse(TestContext context) {
        final Async async = context.async();

        CreateOrderCaptureResponse testResponse = new CreateOrderCaptureResponse("an order capture ID", "a session ID");
        RawJsonResponse jsonResponse = new RawJsonResponse(Json.encode(testResponse));

        orderCaptureClient.setCreateOrderCaptureResponse(jsonResponse);

        vertx.createHttpClient().post(PORT, "localhost", "/v1/order-capture", response -> {
            assertThat(context, response.statusCode(), is(200));

            response.bodyHandler(body -> {
                JsonObject bodyJson = body.toJsonObject();

                context.assertEquals(testResponse.getOrderCaptureId(), bodyJson.getString("orderCaptureId"));
                context.assertEquals(testResponse.getSessionId(), bodyJson.getString("sessionId"));

                async.complete();
            });
        }).end();
    }

    @Test
    public void getOrderCaptureSendsExpectedParameters(TestContext context) {
        final Async async = context.async();

        String orderCaptureId = "o321";
        String token = "89s89dafdsdsSEPS!3jS1";

        orderCaptureClient.setOrderCaptureResponse(new RawJsonResponse("pig, wearing a hat"));

        vertx.createHttpClient()
                .get(PORT, "localhost", "/v1/order-capture/"+orderCaptureId+"?token="+token)
                .handler(response -> {
                    assertThat(context, orderCaptureClient.getLastRequestOrderCaptureId(), is(orderCaptureId));
                    assertThat(context, orderCaptureClient.getLastRequestToken(), is(token));

                    async.complete();
                })
                .end();
    }

    @Test
    public void getOrderCaptureHandlesSuccessResponse(TestContext context) {
        final Async async = context.async();

        String orderCaptureId = "o323";
        String token = "89ssdsFAK!3jS1";

        RawJsonResponse orderCaptureResponse = new RawJsonResponse("horse in high heels");
        orderCaptureClient.setOrderCaptureResponse(orderCaptureResponse);

        vertx.createHttpClient()
                .get(PORT, "localhost", "/v1/order-capture/"+orderCaptureId+"?token="+token)
                .handler(response -> {
                    response.bodyHandler(body -> {
                        assertThat(context, body.toString(), is(orderCaptureResponse.getData()));
                        async.complete();
                    });
                })
                .end();
    }

    @Test
    public void getOrderCaptureHandlesErrorResponse(TestContext context) {
        final Async async = context.async();

        String orderCaptureId = "o323";
        String token = "89ssdsFAK!3jS1";

        ErrorResponse errorResponse = new ErrorResponse("whoops.wrong.basket", "Not yours put it back!");
        RawJsonResponse orderCaptureResponse = new RawJsonResponse
                (HttpResponseStatus.UNAUTHORIZED.code(), Json.encode(errorResponse));

        orderCaptureClient.setOrderCaptureResponse(orderCaptureResponse);

        vertx.createHttpClient()
                .get(PORT, "localhost", "/v1/order-capture/"+orderCaptureId+"?token="+token)
                .handler(response -> {
                    assertThat(context, response.statusCode(), is(HttpResponseStatus.UNAUTHORIZED.code()));

                    response.bodyHandler(body -> {
                        JsonObject jsonError = body.toJsonObject();

                        assertThat(context, jsonError.getString("code"), is(errorResponse.getCode()));
                        assertThat(context, jsonError.getString("message"), is(errorResponse.getMessage()));
                        async.complete();
                    });
                })
                .end();
    }

    @Test
    public void addItemRequestSendsExpectedParameters(TestContext context) {
        final Async async = context.async();

        String orderCaptureId = "o321";

        JsonObject addItemRequest = new JsonObject();
        addItemRequest.put("skuId", "s1234");
        addItemRequest.put("quantity", 7);

        String token = "89s89daxJLKSEPS!3jK3";

        orderCaptureClient.setAddItemResponse(new RawJsonResponse("bowl of mixed nuts"));

        vertx.createHttpClient()
                .post(PORT, "localhost", "/v1/order-capture/"+orderCaptureId+"/items?token="+token)
                .handler(response -> {
                    assertThat(context, orderCaptureClient.getLastRequestOrderCaptureId(), is(orderCaptureId));
                    assertThat(context, orderCaptureClient.getLastRequestToken(), is(token));
                    assertThat(context, orderCaptureClient.getLastAddItemRequest().getSkuId(), is("s1234"));
                    assertThat(context, orderCaptureClient.getLastAddItemRequest().getQuantity(), is(7));

                    async.complete();
                })
                .end(addItemRequest.encodePrettily());
    }

    @Test
    public void addItemReturnsExpectedResponse(TestContext context) {
        final Async async = context.async();

        String orderCaptureId = "o321";

        JsonObject addItemRequest = new JsonObject();
        addItemRequest.put("skuId", "a");
        addItemRequest.put("quantity", 1);

        JsonObject addItemResponse = new JsonObject();
        addItemResponse.put("commerceItemId", "ci1234");
        RawJsonResponse rawJsonResponse = RawJsonResponse.from(addItemResponse);

        orderCaptureClient.setAddItemResponse(rawJsonResponse);

        vertx.createHttpClient()
            .post(PORT, "localhost", "/v1/order-capture/"+orderCaptureId+"/items?token=x")
            .handler(response -> {
                assertThat(context, response.statusCode(), is(200));

                response.bodyHandler(body -> {
                    assertThat(context, body.toString(), is(rawJsonResponse.getData()));

                    async.complete();
                });
            })
            .end(addItemRequest.encodePrettily());
    }
}