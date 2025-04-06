use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/power-wholesale-price",
        get(handlers::power_wholesale_price::get_by_params).post(handlers::power_wholesale_price::create),
    )
    .route(
        "/power-wholesale-price/{id}",
        delete(handlers::power_wholesale_price::delete).put(handlers::power_wholesale_price::update),
    )
}
