# Aviation API Wrapper

A Spring Boot 3 / WebFlux service that wraps the public [aviationapi.com](https://aviationapi.com/) airport endpoint with caching,
resiliency, and observability built in.

## Prerequisites

- Java 21 (Adoptium/Temurin recommended)
- Maven 3.9+ (the project ships with `./mvnw` so you do not need a local installation)
- Docker 24+ (optional, only if you want to run the container image)

## Setup & Run

1. Install dependencies and build once:
   ```bash
   ./mvnw clean package
   ```
2. Run the service in development mode:
   ```bash
   ./mvnw spring-boot:run
   ```
   The API listens on `http://localhost:8080` by default. OpenAPI docs live at `http://localhost:8080/swagger-ui.html`.
3. Run from the shaded jar:
   ```bash
   java -jar target/aviation-api-wrapper-0.0.1-SNAPSHOT.jar
   ```
4. (Optional) Build & run the Docker image:
   ```bash
   docker build -t aviation-api-wrapper .
   docker run --rm -p 8080:8080 aviation-api-wrapper
   ```

### Configuration

Key properties are defined in `src/main/resources/application.yml` and can be overridden via environment variables or the standard
Spring configuration sources:

| Property                          | Env var                           | Purpose                         | Default                                |
|-----------------------------------|-----------------------------------|---------------------------------|----------------------------------------|
| `app.aviation.base-url`           | `APP_AVIATION_BASE_URL`           | Upstream Aviation API base URL  | `https://api.aviationapi.com`          |
| `app.aviation.airports-path`      | `APP_AVIATION_AIRPORTS_PATH`      | Path to airport lookup endpoint | `/v1/airports`                         |
| `app.aviation.connect-timeout-ms` | `APP_AVIATION_CONNECT_TIMEOUT_MS` | WebClient connect timeout       | `2000`                                 |
| `app.aviation.read-timeout-ms`    | `APP_AVIATION_READ_TIMEOUT_MS`    | WebClient read timeout          | `3000`                                 |
| `app.cache.airports.spec`         | `APP_CACHE_AIRPORTS_SPEC`         | Caffeine spec for airport cache | `maximumSize=500,expireAfterWrite=10m` |

## Running Tests

- Run the full test suite (unit + integration + contract):
  ```bash
  ./mvnw verify
  ```
- Faster feedback loop (unit tests only):
  ```bash
  ./mvnw test
  ```

Integration tests rely on MockWebServer, so they do not require network access to aviationapi.com.

## Architecture & Notes

- **Reactive stack**: WebFlux + Reactor are used end-to-end so the service can efficiently multiplex concurrent upstream calls
  without blocking threads.
- **Provider abstraction**: `AirportService` depends on the `AviationProvider` interface so other providers (e.g., a future mock
  or cached dataset) can be swapped in without touching controllers.
- **Resiliency & caching**: `AviationApiClient` uses Resilience4j (circuit breaker + retry + time limiter) around the `WebClient`
  call and caches Mono results in a Caffeine cache to suppress duplicate upstream calls for hot ICAO codes.
- **Observability**: `MetricsService` wraps Micrometer timers for both external calls and high-level operations; Prometheus
  metrics and standard actuator endpoints are exposed (`/actuator/*` plus `/actuator/prometheus`).
- **HTTP surface**: Primary endpoint is `GET /api/airports/{icao}` returning a rich `Airport` document. 404 is returned when the
  upstream has no data for the provided ICAO. OpenAPI docs are generated automatically via springdoc.
- **Assumptions**:
    - AviationAPI’s airport dataset is the single source of truth; there is no local persistence.
    - ICAO codes are the canonical lookup key (FAA/LID is exposed only as part of the payload).
    - Upstream responses may differ in shape; `AviationApiMapper` defensively unwraps either `{icao: {...}}` or lists and
      tolerates missing/invalid numeric fields.
    - The provided defaults for timeouts and cache sizing are tuned for demo workloads; adjust via env vars for production
      traffic.
- **Error handling**:
    - `GlobalExceptionHandler` translates upstream 4xx responses into ProblemDetail payloads with `BAD_REQUEST`/`NOT_FOUND`
      semantics, while unexpected upstream or internal failures become `502 Bad Gateway` or `500 Internal Server Error`.
    - Circuit-breaker fallbacks surface as `502` with a descriptive message and invalidate cached entries to avoid serving stale
      failures.
    - Controller-level 404s are produced when the upstream returns an empty body for the queried ICAO.

Feel free to explore `HELP.md` for additional Maven wrapper tips or to extend the API with additional endpoints (e.g., nearest
airports) by introducing new methods on the `AviationProvider`.


## AI Usage

As part of the development, AI tools were used to assist with code generation and problem-solving. However, all code was 
reviewed, tested, and modified as needed to ensure it meets the project's requirements. In this case, AI was used for:

- Generating the mapper for `Airport` response.
- Reviewing the whole code to find potential issues or problems.
- Helping to redact the `README.md` file
- Helping to set-up integration tests.