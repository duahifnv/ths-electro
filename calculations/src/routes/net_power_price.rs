use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/net-power-price",
        get(handlers::net_power_price::get_by_params).post(handlers::net_power_price::create),
    )
    .route(
        "/fnet-power-price/{id}",
        delete(handlers::net_power_price::delete).put(handlers::net_power_price::update),
    )
}
