package com.johnlewis.contactcentre.bff;

import io.vertx.ext.web.Router;

public interface Routable {
    void routeMe(Router router);
}
