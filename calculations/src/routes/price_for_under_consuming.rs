use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/price-for-under-consuming",
        get(handlers::price_for_under_consuming::get_by_params).post(handlers::price_for_under_consuming::create),
    )
    .route(
        "/price-for-under-consuming/{id}",
        delete(handlers::price_for_under_consuming::delete).put(handlers::price_for_under_consuming::update),
    )
}
