package com.johnlewis.contactcentre.bff.ordercapture.verticle.repository;

import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.verticle.StubCustomerRepository;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.client.StubOrderCaptureApiClient;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.johnlewis.contactcentre.bff.VertxMatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(VertxUnitRunner.class)
public class DefaultOrderCaptureRepositoryTest {

    private Vertx vertx;

    private DefaultOrderCaptureRepository repository;
    private StubOrderCaptureApiClient orderCaptureApiClient;

    private StubCustomerRepository customerRepository;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx().exceptionHandler(context.exceptionHandler());

        customerRepository = new StubCustomerRepository();
        orderCaptureApiClient = new StubOrderCaptureApiClient();

        repository = new DefaultOrderCaptureRepository(orderCaptureApiClient, customerRepository);

        vertx.deployVerticle(repository, context.asyncAssertSuccess());
    }

    @Test
    public void getOrderCaptureAugmentsResponseWithCustomerData(TestContext context) {
        final Async async = context.async();

        String orderCaptureId = "o323";
        String token = "89ssdsFAK!3jS1";

        JsonObject orderCaptureJson = new JsonObject();
        orderCaptureJson.put("id", orderCaptureId);

        RawJsonResponse orderCaptureApiResponse = new RawJsonResponse(orderCaptureJson.encode());
        orderCaptureApiClient.setOrderCaptureResponse(orderCaptureApiResponse);

        Customer customer = Customer.builder().id("c123").name("Mr Carl Weathers").build();
        customerRepository.setGetByIdResponse(customer);

        Map<String, String> ordersToCustomers = new HashMap<>();
        ordersToCustomers.put(orderCaptureId, customer.getId());
        repository.setOrdersToCustomers(ordersToCustomers);

        repository.get(orderCaptureId, token).setHandler(orderCaptureResponse -> {
            JsonObject orderCaptureResponseJson = orderCaptureResponse.result().getData();
            JsonObject customerJson = orderCaptureResponseJson.getJsonObject("customer");

            assertThat(context, customerJson, is(notNullValue()));
            assertThat(context, customerJson.getString("id"), is(customer.getId()));
            assertThat(context, customerJson.getString("name"), is(customer.getName()));

            async.complete();
        });
    }

}