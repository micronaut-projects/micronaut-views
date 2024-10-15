@import io.micronaut.views.fields.elements.InputStringFormElement
@param String type
@param InputStringFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
<input
    type="${type}"
    name="${el.name()}"
    value="${el.value()}"
    id="${el.id()}"
    placeholder="${el.placeholder()}"
    pattern="${el.pattern()}"
    minlength="${el.minLength()}"
    maxlength="${el.maxLength()}"
    size="${el.size()}"
    readOnly="${el.readOnly()}"
    class="form-control is-invalid"
    aria-describedby="${el.name()}ValidationServerFeedback"
    required="${el.required()}"/>
    @template.fieldset.errors(el.name(), el.errors())
@else
<input
    type="${type}"
    name="${el.name()}"
    value="${el.value()}"
    id="${el.id()}"
    placeholder="${el.placeholder()}"
    pattern="${el.pattern()}"
    minlength="${el.minLength()}"
    maxlength="${el.maxLength()}"
    size="${el.size()}"
    readOnly="${el.readOnly()}"
    class="form-control"
    required="${el.required()}"/>
@endif
