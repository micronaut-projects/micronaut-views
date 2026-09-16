# Python Docs Disabled Test Inventory

This file tracks Python docs examples of Micronaut Views that are present but disabled, or that deviate from the
Java example because the direct port currently fails compilation or at runtime. Use it as the bug-fixing task list
for the final migration wave.

## Reconciliation

- Last generated active `@Disabled` count: 0.
- Last generated command: `rg -n "@Disabled\\(" test-suite-python/src/test/python`.
- Last full-suite command: `./gradlew :test-suite-python:test -Ppython-ci`.
- Last full-suite result: build successful, 20 tests executed.

## Migration Rules

- Do not define local copies of Micronaut annotation helpers or custom annotation shims in docs snippets. Standard
  Micronaut annotations are generated from imports. If Python cannot express the import path, keep the intended
  annotation commented out with a TODO and track it below.
- Do not add Java-style getters or setters to Python docs models. Prefer `@dataclass` models with idiomatic Python
  attributes; the Java class generated for an `@Introspected` Python class exposes the attributes to the template
  engines (Velocity `$fruit.getName()`, Thymeleaf `${book.title}`) without any Python getter.
- Methods that implement or override a Java interface keep the Java (camelCase) name; other methods are snake_case.
- Python source files must not live directly in a package that is imported as a Java package: `micronaut/views/*.py`
  would shadow the generated `micronaut.views` stub module (`ModelAndView`, `View`) and the Python compiler fails with
  "Failed to write Python code to [.../micronaut/views/__init__.py]". Tests of `io.micronaut.views.*Test` classes live next
  to the snippet they exercise (`micronaut/views/model/ModelAndViewTest.py`); the Java classes of the packages that the Python
  snippets shadow (`io.micronaut.views.model`, `io.micronaut.views.model.security`) are the only ones referenced with
  `java.type` (see "`java.type` usages" below).
- Prefer normal imports (`from java.io import StringWriter`, `from com.google.template.soy import SoyFileSet`,
  `from .Login import Login`) over `java.type(...)`; the remaining `java.type` calls are listed below with the reason.
- Use Python's `logging` module (`LOG = logging.getLogger(__name__)`, `CustomSoyFileSetProvider.py`), not slf4j.
- Java methods named after a Python keyword are called through the keyword-safe alias (`from_`, ...); never `getattr(obj, "from")`.
- Java classes nested in the Java tests (`TurboFrameBuilderTest.TurboFrameController`, `TurboFrameTest.TurboFrameController`)
  are top-level Python classes; two Python classes with the same name in the same package generate the same Java class and
  silently overwrite each other, so `TurboFrameBuilderTest.py` names its controller `TurboFrameBuilderController`.
- A controller with a `@Valid` argument needs an explicit `@Validated` on the class (`BookController.py`): the Python compiler only
  proxies a class whose class carries an `@Around` stereotype.
- A Python method that overrides a *default* method of a Java interface (`SoyNamingMapProvider.cssRenamingMap()`) is not
  bridged to Java unless it is annotated with `@Executable` (`RewriteMapProvider.py`); without it the default method runs.
- A Java functional interface returned to Java (`SoyCssRenamingMap`) is implemented by a small Python class (`CssRenamingMap`)
  rather than a lambda/method reference.
- Class attribute constants cannot be used in decorators (`@Post(SAVE_PATH)` registers no route); `BookController.py`
  uses the literal `@Post("/save")`.
- A `Class<T>` argument (`FormGenerator.generate(String, Class)`, `HttpRequest.getBody(Class)`,
  `BeanContext.containsBean(Class)`) takes the imported Python class (`from .BookSave import BookSave`); the compiler
  passes its generated Java class.
- A Python `int` attribute maps to a primitive `int`, which the form generator does not render as a number input; use
  `int | None` (`Integer`) for form fields, as the Kotlin suite uses `Int?`.

## Active `@Disabled` Tests

None.

## `java.type` usages

| File | Reason |
| --- | --- |
| `model/ConfigViewModelProcessor.py` (`ViewModelProcessor`) | The Python snippet package `micronaut/views/model` shadows the generated `micronaut.views.model` module, so `from micronaut.views.model import ViewModelProcessor` resolves to the Python package instead of `io.micronaut.views.model` (compiler fix merged, not yet released). |
| `model/security/SecurityViewModelProcessorTest.py` (`SecurityViewModelProcessor`) | Same: the Python snippet package `micronaut/views/model/security` shadows the generated `micronaut.views.model.security` module. |

## Commented Unsupported Snippet Ports

| Target | Reason |
| --- | --- |
| `io.micronaut.views.docs.fieldset.AuthorFetcher` | A Python class cannot implement `io.micronaut.views.fields.fetchers.OptionFetcher`: its abstract methods `generate(Class<T>)` and `generate(T)` are same-arity overloads and Python has no method overloading; the Python compiler generates only one of them and javac fails with "is not abstract and does not override abstract method generate(java.lang.Integer)". The guide renders this snippet for Java, Kotlin and Groovy only (`languages="java,kotlin,groovy"`) with a `[.lang-python]` note. |
| `io.micronaut.views.docs.fieldset.BookAuthorSave` | Uses `@Select(fetcher = AuthorFetcher.class)`, see above. |
| `io.micronaut.views.docs.fieldset.AuthorFetcherTest` | Exercises the two ports above. |

## Workarounds Kept In Tests

| Test | Reason |
| --- | --- |
| `io.micronaut.views.model.security.SecurityViewModelProcessorTest` | The Java test starts three application contexts with different properties from plain JUnit tests; the Python port is two `@MicronautTest` classes (`SecurityViewModelProcessorTest`, `CustomSecurityKeyTest`) because Python tests must be `@MicronautTest` classes and cannot nest manual contexts. |
| `io.micronaut.views.docs.turbo.TurboStreamTemplateTest` | Same: `@MicronautTest(startApplication = false)` with an injected `TurboStreamRenderer` instead of `ApplicationContext.run(...)`. |

## Intentionally Unsupported Snippet Targets

| Target | Reason |
| --- | --- |
| `io.micronaut.views.rocker.docs.HomeController` | Rocker templates are compiled to Java classes (`views.home`) by the Rocker build plugin; the snippet is rendered with `language="java"` from `views-rocker`. |
| `io.micronaut.views.jstachio.pkginfo.HomeController`, `HomeModel`, `package-info` | JStachio generates renderers from `@JStache` Java classes with a Java annotation processor; the snippets are rendered with `language="java"` from `views-jstachio`. |
