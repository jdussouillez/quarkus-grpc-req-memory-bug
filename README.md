# quarkus-grpc-test

Simple project to test stuff with Quarkus and gRPC.

1. Build the spec project

```sh
cd spec && ./mvnw install && cd ..
```

2. Run the server

```sh
cd server && ./mvnw quarkus:dev --pl server-rpc
```

3. Run the client

```sh
cd client && ./mvnw quarkus:dev
```
