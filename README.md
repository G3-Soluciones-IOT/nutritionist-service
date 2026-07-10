# nutritionist-service

Microservicio Spring Boot para la gestion de nutricionistas y pacientes asociados.

## Requisitos

- Java 21+
- Maven Wrapper incluido
- PostgreSQL para ejecucion local con base persistente

## Ejecutar pruebas

```bash
./mvnw verify
```

Las pruebas deshabilitan Config Server/Eureka y usan H2 en memoria.

## Ejecutar local

```bash
./mvnw spring-boot:run
```

Variables principales:

- `SERVER_PORT`, por defecto `8085`
- `CONFIG_SERVER_URI`, por defecto `http://config-service:8888`
- `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DATABASE`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- `JWT_JWKS_URI` o `LEGACY_JWT_JWK_SET_URI` para tokens emitidos por `iam-service`
- `AUTH0_ISSUER_URI` y `AUTH0_AUDIENCE` para tokens Auth0
