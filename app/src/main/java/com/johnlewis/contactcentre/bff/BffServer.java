package com.johnlewis.contactcentre.bff;

import com.johnlewis.contactcentre.bff.customer.verticle.CustomerVerticle;
import com.johnlewis.contactcentre.bff.customer.verticle.repository.FileSystemCustomerRepository;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.OrderCaptureVerticle;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.repository.DefaultOrderCaptureRepository;
import com.johnlewis.contactcentre.bff.product.verticle.ProductVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class BffServer {

    private final Router router;
    private final Vertx vertx;

    private JsonObject config;

    public BffServer() {
        vertx = Vertx.vertx();
        router = Router.router(vertx);

        config = loadJsonConfig();
        DeploymentOptions options = new DeploymentOptions().setConfig(config);

        FileSystemCustomerRepository customerRepository = new FileSystemCustomerRepository();
        DefaultOrderCaptureRepository orderCaptureRepository = new DefaultOrderCaptureRepository(customerRepository);

        vertx.deployVerticle(customerRepository, options);
        vertx.deployVerticle(orderCaptureRepository, options);

        vertx.deployVerticle(new OrderCaptureVerticle(router, orderCaptureRepository), options);
        vertx.deployVerticle(new CustomerVerticle(router, customerRepository), options);
        vertx.deployVerticle(new ProductVerticle(router), options);
    }

    private JsonObject loadJsonConfig() {
        config = new JsonConfigFileLoader().loadAllPresent(
                "config/defaults.json",
                "config/local.json");

        System.out.println("Using configuration:");
        System.out.println("====================");
        System.out.println(config.encodePrettily());
        System.out.println("====================");

        return config;
    }

    public void run() {
        int port = config.getInteger("port");

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port);

        System.out.println("BFF Server listening on port "+port);
    }
}
