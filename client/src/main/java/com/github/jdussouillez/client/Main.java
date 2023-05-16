package com.github.jdussouillez.client;

import com.github.jdussouillez.api.grpc.PdfGrpcApiService;
import com.github.jdussouillez.api.grpc.PdfRequest;
import com.google.protobuf.ByteString;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Multi;

@QuarkusMain
public class Main implements QuarkusApplication {

    @GrpcClient("serv")
    protected PdfGrpcApiService pdfGrpcApiService;

    @Override
    public int run(final String... args) {
        var reqs = Multi.createFrom().range(0, 5)
            .map(i -> String.valueOf(i).repeat(75))
            .map(s -> PdfRequest.newBuilder().setData(ByteString.copyFromUtf8(s)).build());
        pdfGrpcApiService.generate(reqs)
            .collect()
            .asList()
            .invoke(bytes -> System.out.println("Result: " + bytes))
            .replaceWithVoid()
            .await()
            .indefinitely();
        return 0;
    }
}
