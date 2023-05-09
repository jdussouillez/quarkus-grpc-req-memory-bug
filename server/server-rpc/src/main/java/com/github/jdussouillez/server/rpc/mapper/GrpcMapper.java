package com.github.jdussouillez.server.rpc.mapper;

public interface GrpcMapper<J, G> {

    G toGrpc(J javaObj);

    J fromGrpc(G grpcObj);
}
