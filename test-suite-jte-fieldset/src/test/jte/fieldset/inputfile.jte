@import io.micronaut.views.fields.elements.InputFileFormElement
@param InputFileFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
<input type="file"
    name="${el.name()}"
    id="${el.id()}"
    accept="${el.accept()}"
    class="form-control is-invalid"
    aria-describedby="${el.name()}ValidationServerFeedback"
    required="${el.required()}"
    />
    @template.fieldset.errors(el.name(), el.errors())
@else
<input type="file"
    name="${el.name()}"
    id="${el.id()}"
    accept="${el.accept()}"
    class="form-control"
    required="${el.required()}"
    />
@endif
