package io.micronaut.views.docs.turbo;

public final class Message {

    private final Long id;
    private final String name;
    private final String content;

    public Message(Long id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
