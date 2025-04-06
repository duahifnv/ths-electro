use serde::{Deserialize, Serialize};

#[derive(Deserialize)]
pub struct CalculationEasyRequest {
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub price_category_id: i32,
    pub year: u32,
    pub  month: u8,
    pub max_power: f64,
    pub total_power: f64,
}

#[derive(Deserialize)]
pub struct HourUnit {
    pub hour: u8,
    pub power: f64,
}

#[derive(Deserialize)]
pub struct DayUnit {
    pub day: u8,
    pub hour_units: Vec<HourUnit>,
}

#[derive(Deserialize)]
pub struct CalculationHourRequest {
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type: i32,
    pub price_category: i32,
    pub year: u32,
    pub month: u8,
    pub day_units: Vec<DayUnit>,
}

#[derive(Serialize)]
pub struct CalculationResponse {
    pub one: Option<f64>,
    pub two: Option<f64>,
    pub three: Option<f64>,
    pub four: Option<f64>,
    pub five: Option<f64>,
    pub six: Option<f64>
}

