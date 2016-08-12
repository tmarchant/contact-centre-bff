package com.johnlewis.contactcentre.bff;

import io.vertx.ext.unit.TestContext;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/** Enables Hamcrest matchers to report errors correctly while in a Vert.x context.
 *
 *  http://vertx.io/blog/using-hamcrest-matchers-with-vert-x-unit/
 */
public class VertxMatcherAssert {

    public static <T> void assertThat(TestContext context, T actual,
                                      Matcher<? super T> matcher) {
        assertThat(context, "", actual, matcher);
    }

    public static <T> void assertThat(TestContext context, String reason,
                                      T actual, Matcher<? super T> matcher) {
        if (!matcher.matches(actual)) {
            Description description = new StringDescription();
            description.appendText(reason)
                    .appendText("\nExpected: ")
                    .appendDescriptionOf(matcher)
                    .appendText("\n     but: ");
            matcher.describeMismatch(actual, description);
            context.fail(description.toString());
        }
    }

    public static void assertThat(TestContext context, String reason,
                                  boolean assertion) {
        if (!assertion) {
            context.fail(reason);
        }
    }
}
