use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/power-level",
        get(handlers::power_level::get_all).post(handlers::power_level::create),
    )
    .route(
        "/power-level/{id}",
        delete(handlers::power_level::delete).put(handlers::power_level::update),
    )
}
