package io.micronaut.views.fields.tests.books

import geb.Page

class AuthorShowPage extends Page {
    @Override
    String convertToPath(Object[] args) {
        "/author/${args[0]}/show"
    }
    
    static at = { ($('th', text: 'Name') as boolean) && ($('th', text: 'Id') as boolean) }
}
