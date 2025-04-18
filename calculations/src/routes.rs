use axum::Router;

pub mod accounting_hour;
pub mod additional_cost;
pub mod contract_type;
pub mod day_zone;
pub mod fixed_tariff;
pub mod half_peak_tariff;
pub mod hour_tariff;
pub mod net_power_plan_hours;
pub mod net_power_price;
pub mod off_peak_tariff;
pub mod peak_tariff;
pub mod power_level;
pub mod power_wholesale_price;
pub mod price_category;
pub mod price_for_over_consuming;
pub mod price_for_under_consuming;
pub mod voltage_level;
pub mod weekend;

pub fn register_routes(
    app: Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>>,
) -> Router<bb8::Pool<bb8_postgres::PostgresConnectionManager<tokio_postgres::NoTls>>> {
    let app = accounting_hour::register_routes(app);
    let app = additional_cost::register_routes(app);
    let app = contract_type::register_routes(app);
    let app = day_zone::register_routes(app);
    let app = fixed_tariff::register_routes(app);
    let app = half_peak_tariff::register_routes(app);
    let app = hour_tariff::register_routes(app);
    let app = net_power_plan_hours::register_routes(app);
    let app = net_power_price::register_routes(app);
    let app = off_peak_tariff::register_routes(app);
    let app = peak_tariff::register_routes(app);
    let app = power_level::register_routes(app);
    let app = power_wholesale_price::register_routes(app);
    let app = price_category::register_routes(app);
    let app = price_for_over_consuming::register_routes(app);
    let app = price_for_under_consuming::register_routes(app);
    let app = voltage_level::register_routes(app);
    let app = weekend::register_routes(app);
    app
}
