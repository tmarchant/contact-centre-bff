package com.johnlewis.contactcentre.bff.global.domain;

import java.util.List;

public class Cookies {
    public static String extractCookieValue(List<String> cookies, String cookieName) {
        String fullCookie = cookies.stream()
                .filter(c -> c.startsWith(cookieName+"="))
                .findFirst()
                .orElse(null);

        if (fullCookie == null)
            return null;

        int start = fullCookie.indexOf("=");
        int end = fullCookie.length();

        if (fullCookie.indexOf(";") > -1) {
            end = fullCookie.indexOf(";");
        }

        return fullCookie.substring(start+1, end);
    }
}
