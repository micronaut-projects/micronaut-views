package io.micronaut.views.fields;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlTagTest {

    @Test
    void toStringRendersTag() {
        assertEquals("input", HtmlTag.INPUT.toString());
        assertEquals("select", HtmlTag.SELECT.toString());
        assertEquals("div", HtmlTag.DIV.toString());
        assertEquals("textarea", HtmlTag.TEXTAREA.toString());
        assertEquals("option", HtmlTag.OPTION.toString());
        assertEquals("label", HtmlTag.LABEL.toString());
        assertEquals("trix-editor", HtmlTag.TRIX_EDITOR.toString());
    }
}