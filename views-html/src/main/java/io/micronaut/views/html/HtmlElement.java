package io.micronaut.views.html;

import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface HtmlElement {
    @NonNull String getTag();
    @NonNull Map<String, String> getAttributes();
    @Nullable String getContent();
    @Nullable List<? extends HtmlElement> getElements();

    default @Nullable String getClassAttribute() {
        return getAttributes().get("class");
    }

    default @Nullable String getId() {
        return getAttributes().get("id");
    }

    default @NonNull String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(getTag());
        for (String attributeName : getAttributes().keySet()) {
            sb.append(" ");
            sb.append(attributeName);
            sb.append("=");
            sb.append("\"");
            sb.append(getAttributes().get(attributeName));
            sb.append("\"");
        }
        if (StringUtils.isEmpty(getContent()) && CollectionUtils.isEmpty(getElements())) {
            sb.append("/");
            sb.append(">");
        } else {
            sb.append(">");
            if (StringUtils.isNotEmpty(getContent())) {
                sb.append(getContent());
            }
            if (CollectionUtils.isNotEmpty(getElements())) {
                for (HtmlElement element : getElements()) {
                    sb.append(element.toHtml());
                }
            }
            sb.append("<");
            sb.append("/");
            sb.append(getTag());
            sb.append(">");
        }
        return sb.toString();
    }
}
