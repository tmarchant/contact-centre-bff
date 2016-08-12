package com.johnlewis.contactcentre.bff.product.verticle;

import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.product.verticle.client.StubProductApiClient;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.johnlewis.contactcentre.bff.VertxMatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(VertxUnitRunner.class)
public class ProductVerticleTest {
    private final int PORT = 3001;

    private Vertx vertx;
    private ProductVerticle verticle;
    private StubProductApiClient productApiClient;

    @Before
    public void setUp(TestContext context) throws IOException {
        final Async async = context.async();

        vertx = Vertx.vertx().exceptionHandler(context.exceptionHandler());
        Router router = Router.router(vertx);

        productApiClient = new StubProductApiClient();
        verticle = new ProductVerticle(router, productApiClient);

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
    public void searchParametersPassedToApiClient(TestContext context) {
        final Async async = context.async();

        RawJsonResponse searchResponse = new RawJsonResponse("fff");
        productApiClient.setProductSearchResponse(searchResponse);

        vertx.createHttpClient().get(PORT, "localhost", "/v1/products?q=my+search+query&pageSize=101", response -> {
            context.assertEquals("my search query", productApiClient.getLastSearchRequest().getQuery());
            context.assertEquals(101, productApiClient.getLastSearchRequest().getPageSize());

            async.complete();
        }).end();
    }

    @Test
    public void apiClientSearchResponseReturnedUntouched(TestContext context) {
        final Async async = context.async();

        final String atgSearchResponse = "leave me as I am please";

        RawJsonResponse searchResponse = new RawJsonResponse(atgSearchResponse);
        productApiClient.setProductSearchResponse(searchResponse);

        vertx.createHttpClient().get(PORT, "localhost", "/v1/products?q=my+search+query&pageSize=101", response -> {
            assertThat(context, response.statusCode(), is(200));

            response.bodyHandler(handler -> {
                context.assertEquals(atgSearchResponse, handler.toString());
                async.complete();
            });
        }).end();
    }

    @Test
    public void productIdPassedToApiClient(TestContext context) {
        final Async async = context.async();

        RawJsonResponse productResponse = new RawJsonResponse("blurgh");
        productApiClient.setProductDetailsResponse(productResponse);

        vertx.createHttpClient().get(PORT, "localhost", "/v1/products/p12345", response -> {
            context.assertEquals("p12345", productApiClient.getLastProductRequest().getProductId());

            async.complete();
        }).end();
    }

    @Test
    public void productDetailsResponseReturnedUntouched(TestContext context) {
        final Async async = context.async();

        final String atgSearchResponse = "leave me as I am please";

        RawJsonResponse searchResponse = new RawJsonResponse(atgSearchResponse);
        productApiClient.setProductDetailsResponse(searchResponse);

        vertx.createHttpClient().get(PORT, "localhost", "/v1/products/p12345", response -> {
            assertThat(context, response.statusCode(), is(200));

            response.bodyHandler(handler -> {
                context.assertEquals(atgSearchResponse, handler.toString());
                async.complete();
            });
        }).end();
    }
}