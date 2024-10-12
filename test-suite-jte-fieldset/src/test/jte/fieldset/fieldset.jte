@import io.micronaut.views.fields.Fieldset
@import io.micronaut.views.fields.elements.InputCheckboxFormElement
@import io.micronaut.views.fields.elements.InputFormElement
@import io.micronaut.views.fields.elements.InputHiddenFormElement
@import io.micronaut.views.fields.elements.InputNumberFormElement
@import io.micronaut.views.fields.elements.InputEmailFormElement
@import io.micronaut.views.fields.elements.InputTelFormElement
@import io.micronaut.views.fields.elements.InputTextFormElement
@import io.micronaut.views.fields.elements.InputUrlFormElement
@import io.micronaut.views.fields.elements.SelectFormElement
@import io.micronaut.views.fields.elements.InputSubmitFormElement
@import io.micronaut.views.fields.elements.InputPasswordFormElement
@import io.micronaut.views.fields.elements.InputDateFormElement
@import io.micronaut.views.fields.elements.InputDateTimeLocalFormElement
@import io.micronaut.views.fields.elements.TextareaFormElement
@import io.micronaut.views.fields.elements.TrixEditorFormElement
@import io.micronaut.views.fields.elements.InputTimeFormElement
@import io.micronaut.views.fields.elements.InputStringFormElement
@import io.micronaut.views.fields.elements.InputFileFormElement

@param Fieldset el
@for(var field : el.fields())
    @if(field instanceof InputHiddenFormElement inputHiddenFormElement)
            @template.fieldset.inputhidden(inputHiddenFormElement)
    @endif
    @if(field instanceof InputCheckboxFormElement ||
        field instanceof SelectFormElement ||
        field instanceof TextareaFormElement ||
        field instanceof TrixEditorFormElement ||
        field instanceof InputTimeFormElement ||
        field instanceof InputDateFormElement ||
        field instanceof InputDateTimeLocalFormElement ||
        field instanceof InputStringFormElement ||
        field instanceof InputNumberFormElement)
        <div class="mb-3">
            @if(field instanceof InputCheckboxFormElement inputCheckboxFormElement)
                @template.fieldset.inputcheckbox(inputCheckboxFormElement)
            @endif
            @if(field instanceof SelectFormElement selectFormElement)
                @template.fieldset.select(selectFormElement)
            @endif
            @if(field instanceof TextareaFormElement textareaFormElement)
                @template.fieldset.textarea(textareaFormElement)
            @endif
            @if(field instanceof TrixEditorFormElement trixEditorFormElement)
                @template.fieldset.trixeditor(trixEditorFormElement)
            @endif
            @if(field instanceof InputTimeFormElement inputTimeFormElement)
                @template.fieldset.inputtime(inputTimeFormElement)
            @endif
            @if(field instanceof InputDateTimeLocalFormElement inputDateTimeLocalFormElement)
                @template.fieldset.inputdatetimelocal(inputDateTimeLocalFormElement)
            @endif
            @if(field instanceof InputPasswordFormElement inputPasswordFormElement)
                @template.fieldset.inputpassword(inputPasswordFormElement)
            @endif
            @if(field instanceof InputTextFormElement inputTextFormElement)
                @template.fieldset.inputtext(inputTextFormElement)
            @endif
            @if(field instanceof InputNumberFormElement inputNumberFormElement)
                @template.fieldset.inputnumber(inputNumberFormElement)
            @endif
            @if(field instanceof InputEmailFormElement inputEmailFormElement)
                @template.fieldset.inputemail(inputEmailFormElement)
            @endif
            @if(field instanceof InputTelFormElement inputTelFormElement)
                @template.fieldset.inputtel(inputTelFormElement)
            @endif
            @if(field instanceof InputUrlFormElement inputUrlFormElement)
                @template.fieldset.inputurl(inputUrlFormElement)
            @endif
            @if(field instanceof InputDateFormElement inputDateFormElement)
                @template.fieldset.inputdate(inputDateFormElement)
            @endif
        </div>
    @endif
    @if(field instanceof InputFileFormElement inputFileFormElement)
        @template.fieldset.inputfile(inputFileFormElement)
    @endif
    @if(field instanceof InputSubmitFormElement inputSubmitFormElement)
        @template.fieldset.inputsubmit(inputSubmitFormElement)
    @endif
@endfor
