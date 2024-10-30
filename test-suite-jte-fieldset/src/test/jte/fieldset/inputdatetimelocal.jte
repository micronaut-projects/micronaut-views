@import io.micronaut.views.fields.elements.InputDateTimeLocalFormElement
@param InputDateTimeLocalFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
<input
    type="datetime-local"
    name="${el.name()}"
    value="${el.value() != null ? el.value().toString() : null}"
    id="${el.id()}"
    min="${el.min() != null ? el.min().toString() : null}"
    max="${el.max() != null ? el.max().toString() : null}"
    step="${el.step()}"
    class="form-control is-invalid"
    aria-describedby="${el.name()}ValidationServerFeedback"
    required="${el.required()}"
    />
    @template.fieldset.errors(el.name(), el.errors())
@else
<input
    type="datetime-local"
    name="${el.name()}"
    value="${el.value() != null ? el.value().toString() : null}"
    id="${el.id()}"
    min="${el.min() != null ? el.min().toString() : null}"
    max="${el.max() != null ? el.max().toString() : null}"
    step="${el.step()}"
    class="form-control"
    required="${el.required()}"/>
@endif
