package com.johnlewis.contactcentre.bff;

import com.johnlewis.contactcentre.bff.customer.verticle.CustomerVerticle;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.OrderCaptureVerticle;
import com.johnlewis.contactcentre.bff.product.verticle.ProductVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BffServer {

    private final Router router;
    private final Vertx vertx;

    private JsonObject config;

    public BffServer() {
        vertx = Vertx.vertx();
        router = Router.router(vertx);

        config = loadJsonConfigFromPropertiesFiles();
        DeploymentOptions options = new DeploymentOptions().setConfig(config);

        vertx.deployVerticle(new OrderCaptureVerticle(router), options);
        vertx.deployVerticle(new CustomerVerticle(router), options);
        vertx.deployVerticle(new ProductVerticle(router), options);
    }

    private JsonObject loadJsonConfigFromPropertiesFiles() {
        config = new JsonObject();
        Properties properties = new Properties();

        appendPropertiesIfFileExists(properties, "application.properties");
        appendPropertiesIfFileExists(properties, "local.properties");

        System.out.println("Using properties:");
        System.out.println("==================");
        properties.entrySet().stream().forEach(e -> {
            System.out.println(e.getKey()+"="+e.getValue());
        });
        System.out.println("==================");

        properties.entrySet().stream().forEach(e -> {
            config.put((String)e.getKey(), e.getValue());
        });

        return config;
    }

    @SneakyThrows(IOException.class)
    private void appendPropertiesIfFileExists(Properties properties, String fileName) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (stream != null) {
            properties.load(stream);
        }
    }

    public void run() {
        int port = Integer.parseInt(config.getString("port", "3000"));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port);

        System.out.println("BFF Server listening on port "+port);
    }
}
