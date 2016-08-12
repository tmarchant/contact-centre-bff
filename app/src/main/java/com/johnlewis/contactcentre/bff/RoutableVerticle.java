package com.johnlewis.contactcentre.bff;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public abstract class RoutableVerticle extends AbstractVerticle {

    public RoutableVerticle(Router router) {
        routeMe(router);
    }

    protected abstract void routeMe(Router router);
}
