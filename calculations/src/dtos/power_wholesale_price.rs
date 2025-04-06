#[derive(serde::Deserialize)]
pub struct CreateDto {
    pub voltage_level_id: i64,
    pub price_category_id: i64,
    pub power_level_id: i64,
    pub contract_type_id: i64,
    pub price: f64,
    pub year: u32,
    pub month: u8,
}
#[derive(serde::Deserialize)]
pub struct UpdateDto {
    pub price: f64,
}