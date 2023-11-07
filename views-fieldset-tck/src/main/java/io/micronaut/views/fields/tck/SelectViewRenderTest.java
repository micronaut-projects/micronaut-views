/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.fields.tck;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.message.Message;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.fetcher.OptionFetcher;
import io.micronaut.views.fields.elements.SelectFormElement;
import io.micronaut.views.fields.annotations.Select;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Property(name = "spec.name", value = "SelectViewRenderTest")
@MicronautTest(startApplication = false)
@SuppressWarnings({"java:S5960"}) // Assertions are fine, these are tests
class SelectViewRenderTest {

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer) throws IOException {
        SelectFormElement el = SelectFormElement.builder()
            .label(Message.of("Choose a pet:"))
            .id("pet-select")
            .name("pets")
            .options(List.of(
                Option.builder()
                    .value("dog")
                    .label(Message.of("Dog"))
                    .build(),
                Option.builder()
                    .value("cat")
                    .label(Message.of("Cat"))
                    .build(),
                Option.builder()
                    .value("hamster")
                    .label(Message.of("Hamster"))
                    .build(),
                Option.builder()
                    .value("parrot")
                    .label(Message.of("Parrot"))
                    .build(),
                Option.builder()
                    .value("spider")
                    .label(Message.of("Spider"))
                    .build(),
                Option.builder()
                    .value("goldfish")
                    .label(Message.of("Goldfish"))
                    .build()
            ))
            .build();
        assertEquals("""
                <label for="pet-select" class="form-label">Choose a pet:</label><select name="pets" id="pet-select" class="form-select">\
                <option value="dog">Dog</option>\
                <option value="cat">Cat</option>\
                <option value="hamster">Hamster</option>\
                <option value="parrot">Parrot</option>\
                <option value="spider">Spider</option>\
                <option value="goldfish">Goldfish</option>\
                </select>""",
            TestUtils.render("fieldset/select.html", viewsRenderer, Map.of("el", el))
        );
    }

    @Test
    void render(ViewsRenderer<Map<String, Object>, ?> viewsRenderer,
                FieldsetGenerator fieldsetGenerator,
                FormValidator formValidator) throws IOException {
        assertNotNull(viewsRenderer);

        Fieldset fieldset = fieldsetGenerator.generate(new Clinic("cat"));
        assertEquals("""
            <div class="mb-3">\
            <label for="pet" class="form-label">Pet</label>\
            <select name="pet" id="pet" class="form-select" required="required">\
            <option value="dog">Dog</option>\
            <option value="cat" selected="selected">Cat</option>\
            <option value="hamster">Hamster</option>\
            <option value="parrot">Parrot</option>\
            <option value="spider">Spider</option>\
            <option value="goldfish">Goldfish</option>\
            </select>\
            </div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));

        Clinic invalid = new Clinic(null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> formValidator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        assertEquals("""
            <div class="mb-3">\
            <label for="pet" class="form-label">Pet</label>\
            <select name="pet" id="pet" class="form-select is-invalid" aria-describedby="petValidationServerFeedback" required="required">\
            <option value="dog">Dog</option>\
            <option value="cat">Cat</option>\
            <option value="hamster">Hamster</option>\
            <option value="parrot">Parrot</option>\
            <option value="spider">Spider</option>\
            <option value="goldfish">Goldfish</option>\
            </select>\
            <div id="petValidationServerFeedback" class="invalid-feedback">must not be blank</div>\
            </div>""", TestUtils.render("fieldset/fieldset.html", viewsRenderer, Map.of("el", fieldset)));
    }

    @Requires(property = "spec.name", value = "SelectViewRenderTest")
    @Singleton
    static class PetFetcher implements OptionFetcher<String> {

        @Override
        public List<Option> generate(Class<String> type) {
            return options().stream().map(Option.Builder::build).toList();
        }

        private List<Option.Builder> options() {
            return List.of(
                Option.builder().value("dog").label(Message.of("Dog")),
                Option.builder().value("cat").label(Message.of("Cat")),
                Option.builder().value("hamster").label(Message.of("Hamster")),
                Option.builder().value("parrot").label(Message.of("Parrot")),
                Option.builder().value("spider").label(Message.of("Spider")),
                Option.builder().value("goldfish").label(Message.of("Goldfish"))
            );
        }

        @Override
        public List<Option> generate(String instance) {
            List<Option> result = new ArrayList<>();
            for (Option.Builder builder : options()) {
                if (instance.equals(builder.build().value())) {
                    builder.selected(true);
                }
                result.add(builder.build());
            }
            return result;
        }
    }

    @Introspected
    record Clinic(@Select(fetcher = PetFetcher.class) @NotBlank String pet) {
    }
}
