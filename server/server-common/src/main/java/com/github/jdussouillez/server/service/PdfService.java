package com.github.jdussouillez.server.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ApplicationScoped
public class PdfService {

    public Uni<byte[]> generate(final String data) {
        return Uni.createFrom().item(data)
            .map(d -> "I'm your PDF content".getBytes(StandardCharsets.UTF_8))
            .onItem()
            .delayIt()
            .by(Duration.ofSeconds(2L));
    }
}
