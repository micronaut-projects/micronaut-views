@import io.micronaut.views.fields.elements.TrixEditorFormElement
@param TrixEditorFormElement el
@template.fieldset.label(el.id(), el.label())
<input type="hidden" name="${el.name()}" value="${el.value()}" id="${el.id()}"/>
<trix-editor input="${el.id()}"></trix-editor>
