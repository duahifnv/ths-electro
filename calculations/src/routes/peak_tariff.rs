use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/peak-tariff",
        get(handlers::peak_tariff::get_by_params).post(handlers::peak_tariff::create),
    )
    .route(
        "/peak-tariff/{id}",
        delete(handlers::peak_tariff::delete).put(handlers::peak_tariff::update),
    )
}
