package io.micronaut.views.jinjava;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.el.JinjavaInterpreterResolver;
import com.hubspot.jinjava.interpret.FatalTemplateErrorsException;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;
import jinjava.de.odysseus.el.util.SimpleContext;
import jinjava.javax.el.CompositeELResolver;
import jinjava.javax.el.ELResolver;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JinjavaDevelopmentElResolverTest {

    private static final String SPEC = "JinjavaDevelopmentElResolverTest";
    private static final String READ_ONLY_RESOLVER = JinjavaViewsRendererConfigurationProperties.PREFIX + ".config.read-only-resolver";

    @Test
    void outsideDevelopmentModeTheStaticDefaultsAreUsed() {
        try (ApplicationContext context = ApplicationContext.run()) {
            assertSame(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_ONLY, context.getBean(JinjavaConfig.class).getElResolver());
            assertFalse(context.containsBean(JinjavaDevelopmentElResolver.class));
        }
        try (ApplicationContext context = ApplicationContext.run(Map.of(READ_ONLY_RESOLVER, false))) {
            assertSame(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_WRITE, context.getBean(JinjavaConfig.class).getElResolver());
        }
    }

    @Test
    void inDevelopmentModeEachContextHasAResolverOfItsOwn() {
        try (ApplicationContext first = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true));
             ApplicationContext second = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true))) {
            ELResolver resolver = first.getBean(JinjavaConfig.class).getElResolver();
            assertNotSame(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_ONLY, resolver);
            assertNotSame(second.getBean(JinjavaConfig.class).getElResolver(), resolver);
            assertSame(resolver, first.getBean(Jinjava.class).getGlobalConfig().getElResolver());
        }
    }

    @Test
    void theCopyIsMadeOfWhatTheDefaultIsMadeOf() throws ReflectiveOperationException {
        try (ApplicationContext readOnly = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true));
             ApplicationContext readWrite = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, READ_ONLY_RESOLVER, false))) {
            // a Jinjava that changes its default resolvers fails here
            assertEquals(parts(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_ONLY), parts(readOnly.getBean(JinjavaConfig.class).getElResolver()));
            assertEquals(parts(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_WRITE), parts(readWrite.getBean(JinjavaConfig.class).getElResolver()));
        }
    }

    @Test
    void theReadOnlySettingIsThatOfTheDefault() {
        try (ApplicationContext readOnly = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true));
             ApplicationContext readWrite = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, READ_ONLY_RESOLVER, false))) {
            ELResolver own = readOnly.getBean(JinjavaConfig.class).getElResolver();
            ELResolver ownWritable = readWrite.getBean(JinjavaConfig.class).getElResolver();
            assertNotSame(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_WRITE, ownWritable);
            assertEquals(writes(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_ONLY), writes(own));
            assertEquals(writes(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_WRITE), writes(ownWritable));
            assertTrue(writes(own).contains("list: PropertyNotWritableException"), () -> writes(own).toString());
            assertTrue(writes(ownWritable).contains("list: written"), () -> writes(ownWritable).toString());
        }
    }

    @Test
    void aTemplateRendersAsWithTheDefault() {
        String template = """
            property: {{ book.title }}
            boolean property: {{ book.published }}
            snake case property: {{ book.page_count }}
            camel case property: {{ book.pageCount }}
            nested: {{ book.author.name }}
            method: {{ book.quote('one') }}
            method without arguments: {{ book.summary() }}
            missing property: [{{ book.missing }}]
            restricted property: [{{ book.class }}]
            map: {{ map.key }} {{ map['key'] }} [{{ map.missing }}] {{ map.size() }}
            int key: {{ intKeys[1] }}
            list: {{ list[0] }} {{ list[-1] }} {{ list|join(',') }} {{ list.size() }} [{{ list[9] }}]
            array: {{ array[1] }} {{ array|length }}
            bundle: {{ bundle.greeting }}
            filter: {{ book.title|upper }}
            loop: {% for item in books %}{{ item.title }} {% endfor %}
            """;
        Map<String, Object> model = Map.of(
            "book", new Book("Dune"),
            "books", List.of(new Book("Emma"), new Book("Ulysses")),
            "map", Map.of("key", "value"),
            "intKeys", Map.of(1L, "one"),
            "list", List.of("a", "b"),
            "array", new String[] {"x", "y"},
            "bundle", new Bundle());
        String expected = new Jinjava().render(template, model);
        assertTrue(expected.contains("snake case property: 42") && expected.contains("nested: Herbert") && expected.contains("method: Dune one"), expected);
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true))) {
            assertNotSame(JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_ONLY, context.getBean(Jinjava.class).getGlobalConfig().getElResolver());
            assertEquals(expected, context.getBean(Jinjava.class).render(template, model));
            // a restricted method fails the render, the same way
            String restricted = "{{ book.getClass() }}";
            String message = assertThrows(FatalTemplateErrorsException.class, () -> new Jinjava().render(restricted, model)).getMessage();
            assertEquals(message, assertThrows(FatalTemplateErrorsException.class, () -> context.getBean(Jinjava.class).render(restricted, model)).getMessage());
        }
    }

    @Test
    void aResolverTheApplicationSetIsLeftAsItIs() {
        try (ApplicationContext context = ApplicationContext.run(Map.of(DevelopmentMode.PROPERTY, true, "spec.name", SPEC))) {
            assertSame(OwnResolver.RESOLVER, context.getBean(JinjavaConfig.class).getElResolver());
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> parts(ELResolver resolver) throws ReflectiveOperationException {
        Field field = CompositeELResolver.class.getDeclaredField("resolvers");
        field.setAccessible(true);
        return ((List<ELResolver>) field.get(resolver)).stream().map(part -> part.getClass().getName()).toList();
    }

    /**
     * What a write through the resolver does, to each kind of base.
     */
    private static List<String> writes(ELResolver resolver) {
        return List.of(
            write(resolver, "array", new String[] {"a"}, 0),
            write(resolver, "list", new ArrayList<>(List.of("a")), 0),
            write(resolver, "map", new HashMap<>(Map.of("key", "a")), "key"),
            write(resolver, "bean", new Book("Dune"), "title"));
    }

    private static String write(ELResolver resolver, String name, Object base, Object property) {
        SimpleContext context = new SimpleContext();
        try {
            resolver.setValue(context, base, property, "b");
            return name + ": written";
        } catch (RuntimeException e) {
            return name + ": " + e.getClass().getSimpleName();
        }
    }

    @Singleton
    @Requires(property = "spec.name", value = SPEC)
    static final class OwnResolver implements BeanCreatedEventListener<JinjavaViewsRendererConfigurationProperties> {

        static final ELResolver RESOLVER = new CompositeELResolver();

        @Override
        public JinjavaViewsRendererConfigurationProperties onCreated(BeanCreatedEvent<JinjavaViewsRendererConfigurationProperties> event) {
            event.getBean().getConfig().withElResolver(RESOLVER);
            return event.getBean();
        }
    }

    public static final class Bundle extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {{"greeting", "hello"}};
        }
    }

    public static final class Author {
        public String getName() {
            return "Herbert";
        }
    }

    public static final class Book {
        private String title;

        Book(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isPublished() {
            return true;
        }

        public int getPageCount() {
            return 42;
        }

        public Author getAuthor() {
            return new Author();
        }

        public String quote(String word) {
            return title + " " + word;
        }

        public String summary() {
            return "summary of " + title;
        }
    }
}
