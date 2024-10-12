@import io.micronaut.views.fields.elements.InputRadioFormElement
@import io.micronaut.views.fields.elements.Radio
@param InputRadioFormElement el
@param Radio radio
<input
    type="radio"
    name="${el.name()}"
    value="${radio.value()}"
    id="${radio.id()}"
    class="form-check-input"
    required="${el.required()}"
    disabled="${radio.disabled()}"
    checked="${radio.checked()}"/>
