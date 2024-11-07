@import io.micronaut.views.fields.elements.InputNumberFormElement
@param InputNumberFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
<input type="number"
       name="${el.name()}"
       value="${el.value()}"
       id="${el.id()}"
       min="${el.min()}"
       max="${el.max()}"
       step="${el.step()}"
       placeholder="${el.placeholder()}"
       readOnly="${el.readOnly()}"
       class="form-control is-invalid"
        aria-describedby="${el.name()}ValidationServerFeedback"
       required="${el.required()}"/>
    @template.fieldset.errors(el.name(), el.errors())
@else
<input type="number"
       name="${el.name()}"
       value="${el.value()}"
       id="${el.id()}"
       min="${el.min()}"
       max="${el.max()}"
       step="${el.step()}"
       placeholder="${el.placeholder()}"
       readOnly="${el.readOnly()}"
       class="form-control"
       required="${el.required()}"/>
@endif

