@import io.micronaut.views.fields.elements.InputTimeFormElement
@param InputTimeFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
<input
    type="time"
    name="${el.name()}"
    value="${el.value() != null ? el.value().toString() : null}"
    id="${el.id()}"
    min="${el.min() != null ? el.min().toString() : null}"
    max="${el.max() != null ? el.max().toString() : null}"
    class="form-control is-invalid"
    aria-describedby="${el.name()}ValidationServerFeedback"
    required="${el.required()}"
    />
    @template.fieldset.errors(el.name(), el.errors())
@else
<input
    type="time"
    name="${el.name()}"
    value="${el.value() != null ? el.value().toString() : null}"
    id="${el.id()}"
    min="${el.min() != null ? el.min().toString() : null}"
    max="${el.max() != null ? el.max().toString() : null}"
    class="form-control"
    required="${el.required()}"/>
@endif

