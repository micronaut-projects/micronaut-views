package io.micronaut.views.fields;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {

    private TestUtils() {
    }

    @SafeVarargs
    public static <U extends FormElement> void assertAnyMatch(Fieldset fieldset, U... expected) {
        assertAnyMatch(fieldset.fields(), expected);
    }

    @SafeVarargs
    public static <T extends FormElement, U extends FormElement> void assertAnyMatch(List<T> list, U... expected) {
        assertTrue(
            list.stream().anyMatch(e -> {
                    for (U u : expected) {
                        if (u.equals(e)) {
                            return true;
                        }
                    }
                    return false;
                }
            ),
            "Expected %s to contain any of %s".formatted(
                list.stream().map(Object::toString).collect(Collectors.joining("\n- ", "[\n- ", "\n]\n")),
                Stream.of(expected).map(Object::toString).collect(Collectors.joining("\n- ", "[\n- ", "\n]"))
            )
        );
    }

    public static <T extends FormElement, U extends FormElement> void assertAnyInstance(List<T> list, Class<U> expected) {
        assertTrue(
            list.stream().anyMatch(expected::isInstance),
            "Expected one of %s to be an instance of %s".formatted(list, expected)
        );
    }
}
