@import io.micronaut.views.fields.Form
@param Form form
<form action="${form.action()}" method="${form.method()}" enctype="${form.enctype()}">
    @template.fieldset.fieldset(form.fieldset())
</form>
