use axum::{
    Router,
    routing::get,
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/acounting-hour",
        get(handlers::voltage_level::get_all)
            .post(handlers::voltage_level::create)
            .delete(handlers::voltage_level::delete)
            .put(handlers::voltage_level::update),
    )
}
