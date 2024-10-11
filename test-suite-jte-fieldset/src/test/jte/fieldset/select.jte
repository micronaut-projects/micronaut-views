@import io.micronaut.views.fields.elements.SelectFormElement
@param SelectFormElement el
@template.fieldset.label(el.id(), el.label())
@if(el.hasErrors())
    <select name="${el.name()}"
            id="${el.id()}"
            class="form-select is-invalid"
            aria-describedby="${el.name()}ValidationServerFeedback"
            required="${el.required()}">
        @for(var option : el.options())
            @template.fieldset.option(option)
        @endfor
    </select>
    @template.fieldset.errors(el.name(), el.errors())
@else
    <select name="${el.name()}"
            id="${el.id()}"
            class="form-select"
            required="${el.required()}">
        @for(var option : el.options())
            @template.fieldset.option(option)
        @endfor
    </select>
@endif

