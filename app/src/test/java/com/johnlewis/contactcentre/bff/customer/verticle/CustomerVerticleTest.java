package com.johnlewis.contactcentre.bff.customer.verticle;

import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.johnlewis.contactcentre.bff.VertxMatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(VertxUnitRunner.class)
public class CustomerVerticleTest {

    private final int PORT = 3001;

    private Vertx vertx;
    private CustomerVerticle verticle;

    private StubCustomerApiClient customerApiClient;

    @Before
    public void setUp(TestContext context) throws IOException {
        final Async async = context.async();

        vertx = Vertx.vertx().exceptionHandler(context.exceptionHandler());
        Router router = Router.router(vertx);

        customerApiClient = new StubCustomerApiClient();

        verticle = new CustomerVerticle(router, customerApiClient);
        vertx.deployVerticle(verticle, context.asyncAssertSuccess());

        Future<HttpServer> httpServerFuture = Future.future();

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(PORT, httpServerFuture.completer());

        httpServerFuture.setHandler(f -> async.complete());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test(timeout = 10000)
    public void customerSearchReturnsExpectedResponse(TestContext context) {
        final Async async = context.async();

        Customer customer = Customer.builder().id("1").name("Oscar Grouch").build();
        CustomerSearchResults customerSearchResults = new CustomerSearchResults(Arrays.asList(customer));

        customerApiClient.setCustomerSearchResults(customerSearchResults);

        vertx.createHttpClient().get(PORT, "localhost", "/v1/customers?q=blerb")
                .handler(response -> {
                    assertThat(context, response.statusCode(), is(200));

                    response.bodyHandler(body -> {
                        JsonArray jsonResults = body.toJsonObject().getJsonArray("customers");
                        assertThat(context, jsonResults, notNullValue());

                        JsonObject jsonCustomer = jsonResults.getJsonObject(0);
                        assertThat(context, jsonCustomer.getString("name"), is(customer.getName()));

                        async.complete();
                    });
                })
                .end();
    }
}