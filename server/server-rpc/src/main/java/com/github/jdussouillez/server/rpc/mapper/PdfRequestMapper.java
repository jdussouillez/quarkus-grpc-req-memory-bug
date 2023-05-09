package com.github.jdussouillez.server.rpc.mapper;

import com.github.jdussouillez.api.grpc.PdfRequest;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.stream.Collectors;

@ApplicationScoped
public class PdfRequestMapper implements GrpcMapper<String, Collection<PdfRequest>> {

    @Override
    public String fromGrpc(final Collection<PdfRequest> requests) {
        return requests.stream()
            .map(req -> req.getData().toStringUtf8())
            .collect(Collectors.joining());
    }

    @Override
    public Collection<PdfRequest> toGrpc(final String req) {
        throw new UnsupportedOperationException();
    }
}
