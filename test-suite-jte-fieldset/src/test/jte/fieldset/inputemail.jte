@import io.micronaut.views.fields.elements.InputEmailFormElement
@import io.micronaut.views.fields.elements.InputStringFormElement
@param InputEmailFormElement el
@if(el instanceof InputStringFormElement inputStringFormElement)
@template.fieldset.inputstring("email",inputStringFormElement)
@endif

