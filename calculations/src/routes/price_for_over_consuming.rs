use axum::{
    Router,
    routing::{delete, get},
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/price-for-over-consuming",
        get(handlers::price_for_over_consuming::get_by_params).post(handlers::price_for_over_consuming::create),
    )
    .route(
        "/price-for-over-consuming/{id}",
        delete(handlers::price_for_over_consuming::delete).put(handlers::price_for_over_consuming::update),
    )
}
