package com.github.jdussouillez.server.rpc.service;

import com.github.jdussouillez.api.grpc.FileChunk;
import com.github.jdussouillez.api.grpc.PdfGrpcApiService;
import com.github.jdussouillez.api.grpc.PdfRequest;
import com.github.jdussouillez.server.rpc.mapper.FileChunkMapper;
import com.github.jdussouillez.server.rpc.mapper.PdfRequestMapper;
import com.github.jdussouillez.server.service.PdfService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;

@GrpcService
public class PdfApiService implements PdfGrpcApiService {

    @Inject
    protected PdfRequestMapper pdfRequestMapper;

    @Inject
    protected FileChunkMapper fileChunkMapper;

    @Inject
    protected PdfService pdfService;

    @Override
    public Multi<FileChunk> generate(final Multi<PdfRequest> requests) {
        return requests
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
            .collect()
            .asList()
            .map(pdfRequestMapper::fromGrpc)
            .chain(pdfService::generate)
            .map(fileChunkMapper::toGrpc)
            .onItem()
            .disjoint();
    }
}
