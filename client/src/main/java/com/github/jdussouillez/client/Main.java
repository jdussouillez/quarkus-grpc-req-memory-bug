package com.github.jdussouillez.client;

import com.github.jdussouillez.api.grpc.PdfGrpcApiService;
import com.github.jdussouillez.api.grpc.PdfRequest;
import com.google.protobuf.ByteString;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Multi;
import java.util.List;

@QuarkusMain
public class Main implements QuarkusApplication {

    @GrpcClient("serv")
    protected PdfGrpcApiService pdfGrpcApiService;

    @Override
    public int run(final String... args) {
        var data = List.of("FOO", "BAR", "BAZ");
        var reqs = data.stream()
            .map(d -> PdfRequest.newBuilder().setData(ByteString.copyFromUtf8(d)).build())
            .toList();
        pdfGrpcApiService.generate(Multi.createFrom().iterable(reqs))
            .collect()
            .asList()
            .invoke(bytes -> System.out.println("Result: " + bytes))
            .replaceWithVoid()
            .await()
            .indefinitely();
        return 0;
    }
}
