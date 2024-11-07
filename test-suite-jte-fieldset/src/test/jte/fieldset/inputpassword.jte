@import io.micronaut.views.fields.elements.InputPasswordFormElement
@import io.micronaut.views.fields.elements.InputStringFormElement
@param InputPasswordFormElement el
@if(el instanceof InputStringFormElement inputStringFormElement)
@template.fieldset.inputstring("password",inputStringFormElement)
@endif

