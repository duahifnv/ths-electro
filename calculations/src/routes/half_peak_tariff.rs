use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/half-peak-tariff",
        get(handlers::half_peak_tariff::get_by_params).post(handlers::half_peak_tariff::create),
    )
    .route(
        "/half-peak-tariff/{id}",
        delete(handlers::half_peak_tariff::delete).put(handlers::half_peak_tariff::update),
    )
}
