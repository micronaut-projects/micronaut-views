@import io.micronaut.views.fields.elements.InputRadioFormElement
@param InputRadioFormElement el
@for(var radio : el.buttons())
    <div class="form-check">
        @template.fieldset.inputradio(el, radio)
        @template.fieldset.label(radio.id(), radio.label())
    </div>
@endfor
