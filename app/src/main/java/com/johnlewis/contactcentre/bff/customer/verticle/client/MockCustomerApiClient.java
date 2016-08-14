package com.johnlewis.contactcentre.bff.customer.verticle.client;

import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/** Refers to a file-based directory of customers on the classpath - doesn't require external calls.
 *  Use this until a true customer endpoint becomes available */
public class MockCustomerApiClient implements CustomerApiClient {

    @Getter
    private final List<Customer> customers;

    public MockCustomerApiClient() {
        this("data/customers.json");
    }

    @SneakyThrows(IOException.class)
    public MockCustomerApiClient(String dataSource) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(dataSource);
        JsonObject customersJson = new JsonObject(IOUtils.toString(stream, "UTF-8"));

        JsonArray customersList = customersJson.getJsonArray("customers");

        customers = IntStream.range(0, customersList.size())
                .mapToObj(customersList::getJsonObject)
                .map(Customer::from)
                .collect(toList());

        System.out.println("Generated mock customer repository (size: "+customers.size()+")");
    }

    @Override
    public Future<CustomerSearchResults> searchCustomers(String query) {
        List<Customer> matchingCustomers = customers.stream()
                .filter(customer -> matches(customer, query))
                .collect(toList());

        Future<CustomerSearchResults> future = Future.future();
        future.complete(new CustomerSearchResults(matchingCustomers));

        return future;
    }

    private boolean matches(Customer customer, String query) {
        String lcQuery = query.toLowerCase();
        return customer.getName().toLowerCase().contains(lcQuery);
    }
}
