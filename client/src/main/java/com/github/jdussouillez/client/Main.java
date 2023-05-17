package com.github.jdussouillez.client;

import com.github.jdussouillez.api.grpc.PdfGrpcApiService;
import com.github.jdussouillez.api.grpc.PdfRequest;
import com.google.common.io.CharStreams;
import com.google.protobuf.ByteString;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Multi;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@QuarkusMain
public class Main implements QuarkusApplication {

    private static final int CHUNK_SIZE = 5 * 1204; // 5KB

    @GrpcClient("serv")
    protected PdfGrpcApiService pdfGrpcApiService;

    @Override
    public int run(final String... args) {
        var scenario = args[0];
        Multi<PdfRequest> reqs = switch (scenario) {
            case "ok-1r":
                yield createReq("data_65526_bytes", null);
            case "ko-1r":
                yield createReq("data_65527_bytes", null);
            case "ok-xr":
                yield createReq("data_65431_bytes", CHUNK_SIZE);
            case "ko-xr":
                yield createReq("data_65450_bytes", CHUNK_SIZE);
            default:
                throw new IllegalArgumentException("Unknown scenario");
        };
        pdfGrpcApiService.generate(reqs)
            .collect()
            .asList()
            .invoke(bytes -> System.out.println("gRPC server result: " + bytes))
            .replaceWithVoid()
            .await()
            .indefinitely();
        return 0;
    }

    private Multi<PdfRequest> createReq(final String filename, final Integer chunkSize) {
        String data;
        try (var input = Main.class.getResourceAsStream("/pdf/" + filename);
            var inputReader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            data = CharStreams.toString(inputReader);
        } catch (IOException ex) {
            return Multi.createFrom().failure(ex);
        }
        Multi<PdfRequest> reqs;
        if (chunkSize == null) {
            reqs = Multi.createFrom()
                .item(PdfRequest.newBuilder().setData(ByteString.copyFromUtf8(data)).build());
        } else {
            reqs = Multi.createFrom().iterable(partitionBytes(data, chunkSize))
                .map(bytes -> PdfRequest.newBuilder().setData(ByteString.copyFrom(bytes)).build());
        }
        return reqs
            .invoke(r -> System.out.println("Sending gRPC request of size " + r.getData().size()));
    }

    private static List<byte[]> partitionBytes(final String data, final int chunkSize) {
        var bytes = data.getBytes(StandardCharsets.UTF_8);
        var nbChunks = (int) Math.ceil((double) bytes.length / chunkSize);
        var chunks = new ArrayList<byte[]>(nbChunks);
        for (int i = 0; i < nbChunks; i++) {
            int startIdx = i * chunkSize;
            int endIdx = Math.min(startIdx + chunkSize, bytes.length);
            chunks.add(Arrays.copyOfRange(bytes, startIdx, endIdx));
        }
        return chunks;
    }
}
