package com.johnlewis.contactcentre.bff.customer.domain;

import lombok.Data;

import java.util.List;

@Data
public class CustomerSearchResults {
    private final List<Customer> customers;
}
