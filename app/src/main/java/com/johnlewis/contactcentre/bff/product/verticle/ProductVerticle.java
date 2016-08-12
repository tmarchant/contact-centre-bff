package com.johnlewis.contactcentre.bff.product.verticle;

import com.johnlewis.contactcentre.bff.RoutableVerticle;
import com.johnlewis.contactcentre.bff.product.verticle.client.AtgProductApiClient;
import com.johnlewis.contactcentre.bff.product.verticle.client.ProductApiClient;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/** Pass-through API - delegates the request to ATG and returns its JSON response un-molested
 *
 */
public class ProductVerticle extends RoutableVerticle {

    private ProductApiClient productApiClient;

    public ProductVerticle(Router router) {
        this(router, new AtgProductApiClient());
    }

    public ProductVerticle(Router router, ProductApiClient productApiClient) {
        super(router);
        this.productApiClient = productApiClient;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.deployVerticle(productApiClient, result -> {
            System.out.println("ProductVerticle:: started");
            startFuture.complete();
        });
    }

    @Override
    protected void routeMe(Router router) {
        router.get("/v1/products").handler(this::searchProducts);
        router.get("/v1/products/:productId").handler(this::getProductDetails);
    }

    private void searchProducts(RoutingContext routingContext) {
        String query = routingContext.request().params().get("q");
        int pageSize = Integer.parseInt(routingContext.request().params().get("pageSize"));

        System.out.println("Searching products with query: " + query);

        productApiClient.getSearchResults(query, pageSize).setHandler(f -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(f.result().getData());
        });
    }

    private void getProductDetails(RoutingContext routingContext) {
        String productId = routingContext.request().params().get("productId");
        System.out.println("Received product ID: " + productId);

        productApiClient.getProductById(productId).setHandler(f -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(f.result().getData());
        });
    }
}
