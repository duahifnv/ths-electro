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
        get(handlers::accounting_hour::get_all)
            .post(handlers::accounting_hour::create)
            .delete(handlers::accounting_hour::delete)
    )
}
