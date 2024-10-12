@import io.micronaut.views.fields.elements.InputCheckboxFormElement
@param InputCheckboxFormElement el
@if(el.label() != null && el.checkboxes().size() > 1)
@template.fieldset.label(null, el.label())
@endif
@for(var checkbox : el.checkboxes())
    <div class="form-check">
    @if(el.hasErrors())
        <input type="checkbox"
               name="${checkbox.name()}"
               value="${checkbox.value()}"
               id="${checkbox.id()}"
               class="form-check-input is-invalid"
               required="${checkbox.required()}"
               disabled="${checkbox.disabled()}"
               checked="${checkbox.checked()}"/>
    @else
        <input type="checkbox"
               name="${checkbox.name()}"
               value="${checkbox.value()}"
               id="${checkbox.id()}"
               class="form-check-input"
               required="${checkbox.required()}"
               disabled="${checkbox.disabled()}"
               checked="${checkbox.checked()}"/>
    @endif
    @template.fieldset.label(checkbox.id(), checkbox.label())
    </div>
@endfor
