use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/off-peak-tariff",
        get(handlers::off_peak_tariff::get_by_params).post(handlers::off_peak_tariff::create),
    )
    .route(
        "/off-peak-tariff/{id}",
        delete(handlers::off_peak_tariff::delete).put(handlers::off_peak_tariff::update),
    )
}
