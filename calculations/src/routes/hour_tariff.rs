use axum::{
    Router,
    routing::get,
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/hour-tariff",
        get(handlers::hour_tariff::get_all)
            .post(handlers::hour_tariff::create)
            .delete(handlers::hour_tariff::delete)
    )
}
