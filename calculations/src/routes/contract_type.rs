use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/contract_type",
        get(handlers::contract_type::get_all).post(handlers::contract_type::create),
    )
    .route(
        "/contract_type/{id}",
        delete(handlers::contract_type::delete).put(handlers::contract_type::update),
    )
}
