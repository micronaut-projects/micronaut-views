@import io.micronaut.views.fields.elements.TextareaFormElement
@param TextareaFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
<textarea
    name="${el.name()}"
    id="${el.id()}"
    cols="${el.cols()}"
    rows="${el.rows()}"
    class="form-control is-invalid"
    aria-describedby="${el.name()}ValidationServerFeedback">${el.value()}</textarea>
    @template.fieldset.errors(el.name(), el.errors())
@else
<textarea
    name="${el.name()}"
    id="${el.id()}"
    cols="${el.cols()}"
    rows="${el.rows()}"
    class="form-control">${el.value()}</textarea>
@endif

