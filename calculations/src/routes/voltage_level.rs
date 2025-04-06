use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/voltage-level",
        get(handlers::voltage_level::get_all).post(handlers::voltage_level::create),
    )
    .route(
        "/voltage-level/{id}",
        delete(handlers::voltage_level::delete).put(handlers::voltage_level::update),
    )
}
