package io.micronaut.views.fields.thymeleaf;

import io.micronaut.views.fields.message.Message;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.fetcher.OptionFetcher;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class AuthorFetcher implements OptionFetcher<Long> {
    private final AuthorRepository authorRepository;

    public AuthorFetcher(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Option> generate(Class<Long> type) {
        return authorRepository.findAll()
                .stream()
                .map(author -> Option.builder()
                        .value(String.valueOf(author.id()))
                        .label(Message.of(author.title()))
                        .build()
                ).toList();
    }

    @Override
    public List<Option> generate(Long instance) {
        return authorRepository.findAll()
                .stream()
                .map(author -> Option.builder()
                        .selected(author.id().equals(instance))
                        .value(String.valueOf(author.id()))
                        .label(Message.of(author.title()))
                        .build())
                .toList();
    }
}
