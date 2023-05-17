# quarkus-grpc-req-memory-bug

Simple project to test stuff with Quarkus and gRPC.

## Build

1. Build the spec project

```sh
cd spec && ./mvnw install && cd ..
```

2. Run the server

```sh
cd server && \
    ./mvnw package && \
    java -jar server-rpc/target/quarkus-app/quarkus-run.jar
```

3. Build the client

```sh
cd client && ./mvnw package
```

## Client

### Test 0: all the file content (65526 bytes) in a single gRPC request :heavy_check_mark:

```sh
java -jar target/quarkus-app/quarkus-run.jar "ok-1r"
```

```
2023-05-17 13:42:06,808 INFO  [io.qua.grp.run.sup.Channels] (main) Creating Vert.x gRPC channel ...
Sending gRPC request of size 65526
gRPC server result: [content: "I\'m your PDF content"
]
2023-05-17 13:42:09,225 INFO  [io.qua.grp.run.sup.Channels] (main) Shutting down Vert.x gRPC channel io.grpc.ClientInterceptors$InterceptorChannel@5ddabb18
2023-05-17 13:42:09,237 INFO  [io.quarkus] (main) client stopped in 0.028s
```

### Test 1: all the file content (65527 bytes) in a single gRPC request :red_circle:

```sh
java -jar target/quarkus-app/quarkus-run.jar "ko-1r"
```

```
2023-05-17 13:45:37,435 INFO  [io.qua.grp.run.sup.Channels] (main) Creating Vert.x gRPC channel ...
Sending gRPC request of size 65527
2023-05-17 13:45:37,597 ERROR [io.qua.run.Application] (main) Failed to start application (with profile [prod]): io.vertx.core.http.HttpClosedException: Connection was closed

2023-05-17 13:45:37,611 INFO  [io.qua.grp.run.sup.Channels] (main) Shutting down Vert.x gRPC channel io.grpc.ClientInterceptors$InterceptorChannel@1de5f0ef
```

### Test 3: X requests containing small chunks of file content (65431 bytes) :heavy_check_mark:

```sh
java -jar target/quarkus-app/quarkus-run.jar "ok-xr"
```

```
2023-05-17 13:46:04,034 INFO  [io.qua.grp.run.sup.Channels] (main) Creating Vert.x gRPC channel ...
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 5231
gRPC server result: [content: "I\'m your PDF content"
]
2023-05-17 13:46:06,238 INFO  [io.qua.grp.run.sup.Channels] (main) Shutting down Vert.x gRPC channel io.grpc.ClientInterceptors$InterceptorChannel@5ef0d29e
2023-05-17 13:46:06,249 INFO  [io.quarkus] (main) client stopped in 0.026s
```

### Test 4: X requests containing small chunks of file content (65450 bytes) :red_circle:

```sh
java -jar target/quarkus-app/quarkus-run.jar "ko-xr"
```

```
2023-05-17 13:47:43,955 INFO  [io.qua.grp.run.sup.Channels] (main) Creating Vert.x gRPC channel ...
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 6020
Sending gRPC request of size 5250
2023-05-17 13:47:44,119 ERROR [io.ver.cor.htt.imp.Http2UpgradeClientConnection] (vert.x-eventloop-thread-7) Connection was closed: io.vertx.core.VertxException: Connection was closed
2023-05-17 13:47:48,033 INFO  [io.qua.grp.run.sup.Channels] (Shutdown thread) Shutting down Vert.x gRPC channel io.grpc.ClientInterceptors$InterceptorChannel@55242bc1
```

## Error

The error in Quarkus dev mode has more details: `io.vertx.core.http.HttpClosedException: Connection was closed (GOAWAY error code = 3)`.

The error only seems to happen with a server using Quarkus v3 and a client using Quarkus v2. I found two ways to fix it:
- Downgrade the Quarkus version to `2.16.7.Final` in the server module ([patch file](./server/downgrade-v2.diff))
- Upgrade the Quarkus version to `3.0.3.Final` in the client module
