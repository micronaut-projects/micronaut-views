@import io.micronaut.views.fields.elements.InputUrlFormElement
@import io.micronaut.views.fields.elements.InputStringFormElement
@param InputUrlFormElement el
@if(el instanceof InputStringFormElement inputStringFormElement)
@template.fieldset.inputstring("url",inputStringFormElement)
@endif

