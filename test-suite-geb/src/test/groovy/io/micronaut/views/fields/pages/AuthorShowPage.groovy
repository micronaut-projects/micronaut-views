package io.micronaut.views.fields.pages

import geb.Page

class AuthorShowPage extends Page {
    static url = "/contact/list"
    static at = { title == "List Contact" }
}
