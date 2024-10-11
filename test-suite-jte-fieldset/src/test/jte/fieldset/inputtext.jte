@import io.micronaut.views.fields.elements.InputTextFormElement
@import io.micronaut.views.fields.elements.InputStringFormElement
@param InputTextFormElement el
@if(el instanceof InputStringFormElement inputStringFormElement)
@template.fieldset.inputstring("text",inputStringFormElement)
@endif

