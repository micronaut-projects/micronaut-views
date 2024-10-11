@import io.micronaut.views.fields.Fieldset
@import io.micronaut.views.fields.elements.InputFormElement
@import io.micronaut.views.fields.elements.InputHiddenFormElement
@param Fieldset el
@for(var field : el.fields())
    @if(field instanceof InputFormElement inputFormElement)
        @if(field instanceof InputHiddenFormElement inputHiddenFormElement)
            @template.fieldset.inputhidden(inputHiddenFormElement)
        @endif
    @endif
@endfor
