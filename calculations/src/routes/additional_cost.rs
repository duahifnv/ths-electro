use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/additional-cost",
        get(handlers::additional_cost::get_all).post(handlers::additional_cost::create),
    )
    .route(
        "/additional-cost/{id}",
        delete(handlers::additional_cost::delete).put(handlers::additional_cost::update),
    )
}
