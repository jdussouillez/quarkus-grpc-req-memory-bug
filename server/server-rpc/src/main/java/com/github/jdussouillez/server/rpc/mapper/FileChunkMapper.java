package com.github.jdussouillez.server.rpc.mapper;

import com.github.jdussouillez.api.grpc.FileChunk;
import com.github.jdussouillez.server.utils.ByteUtils;
import com.google.protobuf.ByteString;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;

@ApplicationScoped
public class FileChunkMapper implements GrpcMapper<byte[], Collection<FileChunk>> {

    /**
     * Chunk size in bytes
     */
    protected static final int CHUNK_SIZE = 2;

    @Override
    public Collection<FileChunk> toGrpc(final byte[] bytes) {
        return ByteUtils.partition(bytes, CHUNK_SIZE)
            .stream()
            .map(b -> FileChunk.newBuilder().setContent(ByteString.copyFrom(b)).build())
            .toList();
    }

    @Override
    public byte[] fromGrpc(final Collection<FileChunk> chunks) {
        throw new UnsupportedOperationException();
    }
}
