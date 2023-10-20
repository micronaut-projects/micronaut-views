package com.projectcheckins.controllers;

import com.projectcheckins.services.*;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.InputSubmitFormElement;
import io.micronaut.views.fields.Message;
import io.micronaut.views.turbo.TurboFrameView;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller(QuestionController.CONTROLLER_PATH)
class QuestionController {

    public static final String CONTROLLER_PATH = "/questions";
    private static final String MODEL_KEY_FORM = "form";
    private static final String MODEL_KEY_QUESTIONS = "questions";
    public static final String CREATE_VIEW = "question/create.html";
    public static final String ACTION_LIST = "list";
    public static final String ACTION_SAVE = "save";

    public static final String ACTION_DELETE = "delete";

    public static final String ACTION_UPDATE = "update";
    public static final String VIEW_EDIT = "question/edit.html";
    public static final String VIEW_LIST = "question/_list.html";
    private final ConversionService conversionService;
    private final QuestionService questionService;
    private final FormGenerator formGenerator;

    private final URI listPath;
    private final String savePath;

    private final String deletePath;

    private final String updatePath;

    private final InputSubmitFormElement saveSubmit;

    private final InputSubmitFormElement deleteSubmit;

    private final InputSubmitFormElement updateSubmit;

    QuestionController(ConversionService conversionService,
                       QuestionService questionService,
                       FormGenerator formGenerator) {
        this.conversionService = conversionService;
        this.questionService = questionService;
        this.formGenerator = formGenerator;
        listPath = UriBuilder.of(CONTROLLER_PATH).path(ACTION_LIST).build();
        savePath = UriBuilder.of(CONTROLLER_PATH).path(ACTION_SAVE).build().toString();
        deletePath = UriBuilder.of(CONTROLLER_PATH).path(ACTION_DELETE).build().toString();
        updatePath = UriBuilder.of(CONTROLLER_PATH).path(ACTION_UPDATE).build().toString();
        this.saveSubmit = new InputSubmitFormElement(Message.of("Start collecting answers", "question.new.submit"));
        this.deleteSubmit = new InputSubmitFormElement(Message.of("Delete", "question.delete.submit"));
        this.updateSubmit = new InputSubmitFormElement(Message.of("Update", "question.update.submit"));
    }

    @Produces(MediaType.TEXT_HTML)
    @Get
    HttpResponse<?> index() {
        return HttpResponse.seeOther(listPath);
    }

    @Produces(MediaType.TEXT_HTML)
    @View("question/list.html")
    @TurboFrameView("question/_list.html")
    @Get("/list")
    Map<String, Object> list() {
        return listModel();
    }

    private Map<String, Object> listModel() {
        List<QuestionRow> questions = questionService.findAll();
        return Collections.singletonMap(MODEL_KEY_QUESTIONS, questions);
    }

    @Produces(MediaType.TEXT_HTML)
    @View(CREATE_VIEW)
    @TurboFrameView("fieldset/_form.html")
    @Get("/create")
    Map<String, Object> create() {
        Form form = formGenerator.generate(savePath,
            HttpMethod.POST.toString(),
            QuestionSave.class,
            saveSubmit
            );
        return Collections.singletonMap(MODEL_KEY_FORM, form);
    }

    @Produces(MediaType.TEXT_HTML)
    @View("question/show.html")
    @TurboFrameView("fieldset/_show.html")
    @Get("/{questionId}/show")
    HttpResponse<?> show(@PathVariable Long questionId) {
        Optional<QuestionRow> questionOptional = questionService.findById(questionId);
        if (questionOptional.isEmpty()) {
            return HttpResponse.seeOther(NotFoundController.CONTROLLER_URI);
        }
        QuestionRow question = questionOptional.get();
        Form deleteForm = formGenerator.generate(deletePath,
            HttpMethod.POST.toString(),
            new QuestionDelete(questionId),
            deleteSubmit
        );
        return HttpResponse.ok(Map.of("question", question, "deleteForm", deleteForm));
    }

    @Produces(MediaType.TEXT_HTML)
    @View(VIEW_EDIT)
    @TurboFrameView("fieldset/_form.html")
    @Get("/{questionId}/edit")
    HttpResponse<?> edit(@PathVariable Long questionId) {
        Optional<QuestionUpdate> questionOptional = questionService.findUpdateForm(questionId);
        if (questionOptional.isEmpty()) {
            return HttpResponse.seeOther(NotFoundController.CONTROLLER_URI);
        }
        QuestionUpdate question = questionOptional.get();
        Form form = formGenerator.generate(updatePath, HttpMethod.POST.toString(), question, updateSubmit);
        return HttpResponse.ok(Collections.singletonMap(QuestionController.MODEL_KEY_FORM, form));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/save")
    HttpResponse<?> save(@Body Map<String, Object> body,
                         HttpRequest<?> request) {
        QuestionSave questionSave = questionNewFromMap(body);
        try {
            questionService.save(questionSave);
        } catch (ConstraintViolationException constraintViolationException) {
            Form form = formGenerator.generate(savePath,
                HttpMethod.POST.toString(),
                questionSave,
                constraintViolationException,
                saveSubmit
            );
            Map<String, Object> model = Collections.singletonMap(MODEL_KEY_FORM, form);
            return ResponseUtils.respond(request,
                () -> HttpResponse.ok(new ModelAndView<>(CREATE_VIEW, model)),
                turboStream -> turboStream.replace()
                    .template(CREATE_VIEW, model));
        }
        return ResponseUtils.respond(request,
            () -> HttpResponse.seeOther(listPath),
            turboStream -> turboStream.replace()
                .template(VIEW_LIST, listModel()));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/delete")
    HttpResponse<?> delete(@Body @NotNull @Valid QuestionDelete form,
                           HttpRequest<?> request) {
        questionService.delete(form);
        return ResponseUtils.respond(request,
            () -> HttpResponse.seeOther(listPath),
            turboStream -> turboStream.replace()
                .template(VIEW_LIST, listModel()));
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/update")
    HttpResponse<?> update(@Body Map<String, Object> body,
                           HttpRequest<?> request) {
        QuestionUpdate questionUpdate = questionUpdateFromMap(body);
        try {
            questionService.update(questionUpdate);
        } catch (ConstraintViolationException constraintViolationException) {
            Form form = formGenerator.generate(updatePath,
                HttpMethod.POST.toString(),
                questionUpdate,
                constraintViolationException,
                updateSubmit
            );
            Map<String, Object> model = Collections.singletonMap(MODEL_KEY_FORM, form);
            return ResponseUtils.respond(request,
                () -> HttpResponse.ok(new ModelAndView<>(VIEW_EDIT, model)),
                turboStream -> turboStream.replace()
                    .template(VIEW_EDIT, model));
        }
        return ResponseUtils.respond(request,
            () -> HttpResponse.seeOther(listPath),
            turboStream -> turboStream.replace()
                .template(VIEW_LIST, listModel()));
    }

    // This should not be necessary
    private QuestionUpdate questionUpdateFromMap(Map<String, Object> body) {
        return new QuestionUpdate(conversionService.convert(body.get("questionId"), Long.class).orElse(null),
            conversionService.convert(body.get("question"), Argument.STRING).orElse(null),
            conversionService.convert(body.get("onceAWeekOn"), DayOfWeek.class).orElse(null),
            conversionService.convert(body.get("timeOfDay"), String.class).map(LocalTime::parse).orElse(null),
            conversionService.convert(body.get("usersId"), Argument.listOf(Long.class)).orElse(null));
    }

    // This should not be necessary
    private QuestionSave questionNewFromMap(Map<String, Object> body) {
        return new QuestionSave(conversionService.convert(body.get("question"), Argument.STRING).orElse(null),
            conversionService.convert(body.get("onceAWeekOn"), DayOfWeek.class).orElse(null),
            conversionService.convert(body.get("timeOfDay"), String.class).map(LocalTime::parse).orElse(null),
            conversionService.convert(body.get("usersId"), Argument.listOf(Long.class)).orElse(null));
    }
}
