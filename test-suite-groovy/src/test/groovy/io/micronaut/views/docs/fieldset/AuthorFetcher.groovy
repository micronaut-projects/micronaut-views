package io.micronaut.views.docs.fieldset

import io.micronaut.views.fields.elements.Option
import io.micronaut.views.fields.fetchers.OptionFetcher
import io.micronaut.views.fields.messages.Message
import jakarta.inject.Singleton

//tag::clazz[]
@Singleton
class AuthorFetcher implements OptionFetcher<Long> {
    private final AuthorRepository authorRepository

    AuthorFetcher(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository
    }

    @Override
    List<Option> generate(Class<Long> type) {
        authorRepository.findAll()
                .collect { author -> Option.builder()
                        .value(String.valueOf(author.id))
                        .label(Message.of(author.title))
                        .build()
                }
    }

    @Override
    List<Option> generate(Long instance) {
        authorRepository.findAll()
                .collect { author -> Option.builder()
                        .selected(author.id == instance)
                        .value(String.valueOf(author.id))
                        .label(Message.of(author.title))
                        .build()
                }
    }
}
//end::clazz[]
