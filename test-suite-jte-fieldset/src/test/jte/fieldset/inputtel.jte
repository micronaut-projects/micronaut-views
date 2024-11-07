@import io.micronaut.views.fields.elements.InputTelFormElement
@import io.micronaut.views.fields.elements.InputStringFormElement
@param InputTelFormElement el
@if(el instanceof InputStringFormElement inputStringFormElement)
@template.fieldset.inputstring("tel",inputStringFormElement)
@endif

