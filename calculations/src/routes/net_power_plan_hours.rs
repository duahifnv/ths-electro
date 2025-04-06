use axum::{
    Router,
    routing::get,
};

use crate::handlers;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    app.route(
        "/net-power-plan-hours",
        get(handlers::net_power_plan_hours::get_all)
            .post(handlers::net_power_plan_hours::create)
            .delete(handlers::net_power_plan_hours::delete)
    )
}
