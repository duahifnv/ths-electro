use axum::{
    Router,
    routing::get,
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/weekend",
        get(handlers::weekend::get_all)
            .post(handlers::weekend::create)
            .delete(handlers::weekend::delete)
    )
}
