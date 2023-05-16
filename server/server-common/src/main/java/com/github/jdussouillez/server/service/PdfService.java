package com.github.jdussouillez.server.service;

import com.github.jdussouillez.server.Loggers;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ApplicationScoped
public class PdfService {

    public Uni<byte[]> generate(final String data) {
        // TODO: generate real PDF content
        return Uni.createFrom().item(data)
            .invoke(d -> Loggers.MAIN.info("Data for generation: {}", d))
            .map(d -> d.getBytes(StandardCharsets.UTF_8))
            .onItem()
            .delayIt()
            .by(Duration.ofSeconds(2L));
    }
}
