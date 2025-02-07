@import io.micronaut.views.fields.messages.Message
@param String id
@param Message el
<label for="${id != null ? id : null}" class="form-label">${el.defaultMessage()}</label>
