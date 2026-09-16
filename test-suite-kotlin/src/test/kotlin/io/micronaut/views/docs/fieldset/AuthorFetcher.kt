package io.micronaut.views.docs.fieldset

import io.micronaut.views.fields.elements.Option
import io.micronaut.views.fields.fetchers.OptionFetcher
import io.micronaut.views.fields.messages.Message
import jakarta.inject.Singleton

//tag::clazz[]
@Singleton
class AuthorFetcher(private val authorRepository: AuthorRepository) : OptionFetcher<Long> {

    override fun generate(type: Class<Long>): List<Option> {
        return authorRepository.findAll()
            .map { author ->
                Option.builder()
                    .value(author.id.toString())
                    .label(Message.of(author.title))
                    .build()
            }
    }

    override fun generate(instance: Long): List<Option> {
        return authorRepository.findAll()
            .map { author ->
                Option.builder()
                    .selected(author.id == instance)
                    .value(author.id.toString())
                    .label(Message.of(author.title))
                    .build()
            }
    }
}
//end::clazz[]
