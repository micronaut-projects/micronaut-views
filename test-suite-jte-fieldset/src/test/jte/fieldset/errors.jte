@import java.util.List
@import io.micronaut.views.fields.messages.Message
@param String name
@param List<Message> errors
@for(var error: errors)
<div id="${name}ValidationServerFeedback" class="invalid-feedback">${error.defaultMessage()}</div>
@endfor
