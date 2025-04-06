use axum::{
    Router,
    routing::get,
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/day-zone",
        get(handlers::day_zone::get_all)
            .post(handlers::day_zone::create)
            .delete(handlers::day_zone::delete)
    )
}
