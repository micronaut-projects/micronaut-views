@import io.micronaut.views.fields.Form
@param Form form
@if(form.dataturbo() != null)
<form action="${form.action()}" method="${form.method()}" enctype="${form.enctype()}" data-turbo="${form.dataturbo() ? "true": "false"}">
    @template.fieldset.fieldset(form.fieldset())
</form>
@else
<form action="${form.action()}" method="${form.method()}" enctype="${form.enctype()}">
    @template.fieldset.fieldset(form.fieldset())
</form>
@endif

