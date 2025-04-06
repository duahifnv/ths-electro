use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/price-category",
        get(handlers::price_category::get_all).post(handlers::price_category::create),
    )
    .route(
        "/price-category/{id}",
        delete(handlers::price_category::delete).put(handlers::price_category::update),
    )
}
