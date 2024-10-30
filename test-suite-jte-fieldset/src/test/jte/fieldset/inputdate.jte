@import io.micronaut.views.fields.elements.InputDateFormElement
@param InputDateFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
<input
    type="date"
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
    type="date"
    name="${el.name()}"
    value="${el.value() != null ? el.value().toString() : null}"
    id="${el.id()}"
    min="${el.min() != null ? el.min().toString() : null}"
    max="${el.max() != null ? el.max().toString() : null}"
    step="${el.step()}"
    class="form-control"
    required="${el.required()}"/>
@endif
