@import io.micronaut.views.fields.elements.InputHiddenFormElement
@param InputHiddenFormElement el
@if(el.value() != null)
<input type="hidden" name="${el.name()}" value="${el.value()}"/>
@else
<input type="hidden" name="${el.name()}" value=""/>
@endif