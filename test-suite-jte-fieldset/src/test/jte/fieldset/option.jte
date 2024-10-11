@import io.micronaut.views.fields.elements.Option
@param Option el
<option value="${el.value()}" selected="${el.selected()}" disabled="${el.disabled()}">${el.label().defaultMessage()}</option>
