use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/fixed-tariff",
        get(handlers::fixed_tariff::get_by_params).post(handlers::fixed_tariff::create),
    )
    .route(
        "/fixed-tariff/{id}",
        delete(handlers::fixed_tariff::delete).put(handlers::fixed_tariff::update),
    )
}
