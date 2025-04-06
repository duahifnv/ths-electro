FROM rust:1.85.0 as builder
RUN apt-get update && apt-get install -y \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app
COPY Cargo.toml Cargo.lock ./
COPY src ./src
RUN cargo build --release 
FROM debian:bookworm-slim
RUN apt-get update && apt-get install -y \
    libpq5 \
    && rm -rf /var/lib/apt/lists/*
COPY --from=builder /usr/src/app/target/release/electricity-hack-calculation-service /usr/local/bin/electricity-hack-calculation-service
EXPOSE 3000
CMD ["electricity-hack-calculation-service"]