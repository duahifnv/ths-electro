#[derive(serde::Serialize, serde::Deserialize)]
pub struct PriceUnit {
    pub hour: u8,
    pub price: f64,
}
#[derive(serde::Serialize, serde::Deserialize)]
pub struct Unit {
    pub day: u8,
    pub hour_price_units: Vec<PriceUnit>,
}
#[derive(serde::Serialize, serde::Deserialize)]
pub struct Dto {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
    pub units: Vec<Unit>,
}