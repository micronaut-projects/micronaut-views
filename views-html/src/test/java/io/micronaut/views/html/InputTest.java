package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InputTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Input.class));
    }

    @Test
    void toRadioHtml() {
        String html = Input.builder()
                .id("css")
                .name("fav_language")
                .value("CSS")
                .radio()
                .build()
                .toHtml();
        assertEquals("""
                <input id="css" name="fav_language" value="CSS" type="radio"/>""", html);
    }

    @Test
    void toTextHtml() {
        String html = Input.builder()
                .text()
                .name("username")
                .placeholder("Enter username")
                .build()
                .toHtml();
        assertEquals("""
                <input type="text" name="username" placeholder="Enter username"/>""", html);
    }

    @Test
    void toEmailHtml() {
        String html = Input.builder()
                .email()
                .name("email")
                .required()
                .build()
                .toHtml();
        assertEquals("""
                <input type="email" name="email" required="required"/>""", html);
    }

    @Test
    void toPasswordHtml() {
        String html = Input.builder()
                .password()
                .name("pass")
                .build()
                .toHtml();
        assertEquals("""
                <input type="password" name="pass"/>""", html);
    }

    @Test
    void toCheckboxHtml() {
        String html = Input.builder()
                .checkbox()
                .name("agree")
                .value("yes")
                .build()
                .toHtml();
        assertEquals("""
                <input type="checkbox" name="agree" value="yes"/>""", html);
    }

    @Test
    void toHiddenHtml() {
        String html = Input.builder()
                .hidden()
                .name("token")
                .value("abc123")
                .build()
                .toHtml();
        assertEquals("""
                <input type="hidden" name="token" value="abc123"/>""", html);
    }

    @Test
    void toNumberHtml() {
        String html = Input.builder()
                .number()
                .name("quantity")
                .build()
                .toHtml();
        assertEquals("""
                <input type="number" name="quantity"/>""", html);
    }

    @Test
    void toSubmitHtml() {
        String html = Input.builder()
                .submit()
                .value("Send")
                .build()
                .toHtml();
        assertEquals("""
                <input type="submit" value="Send"/>""", html);
    }

    @Test
    void toDateHtml() {
        String html = Input.builder()
                .date()
                .name("birthday")
                .build()
                .toHtml();
        assertEquals("""
                <input type="date" name="birthday"/>""", html);
    }

    @Test
    void toFileHtml() {
        String html = Input.builder()
                .file()
                .name("upload")
                .build()
                .toHtml();
        assertEquals("""
                <input type="file" name="upload"/>""", html);
    }
}
