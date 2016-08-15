package com.johnlewis.contactcentre.bff.customer.domain;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
    private final String id;
    private final String name;
    private final String phone;
    private final String statusDescription;
    private final String myJlNumber;
    private final String dob;
    private final String homePhone;
    private final String workPhone;
    private final String mobilePhone;
    private final String optionalAddressee;
    private final String nameOrNumber;
    private final String street;
    private final String town;
    private final String postcode;
    private final String country;
    private final String state;

    public static Customer from(JsonObject customerJson) {
        return Customer.builder()
                .id(customerJson.getString("id"))
                .name(customerJson.getString("name"))
                .build();
    }

    public boolean matches(String customerId) {
        return this.id.equals(customerId);
    }
}
