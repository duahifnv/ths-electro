use axum::Router;
use electricity_hack_calculation_service::{
    data::dbcontext::{create_pool, create_tables},
    routes::register_routes,
};
use std::net::SocketAddr;
use tower_http::cors::{Any, CorsLayer};

#[tokio::main]
async fn main() {
    let pool = create_pool().await.expect("Failed to create pool");
    create_tables(&pool).await.unwrap();

    let cors = CorsLayer::new()
        .allow_origin(Any)
        .allow_methods(Any)
        .allow_headers(Any);

    let app = Router::new();
    let app = register_routes(app).layer(cors).with_state(pool);

    let addr = SocketAddr::from(([0, 0, 0, 0], 3000));

    let listener = tokio::net::TcpListener::bind(addr).await.unwrap();
    axum::serve(listener, app.into_make_service())
        .await
        .unwrap();
}