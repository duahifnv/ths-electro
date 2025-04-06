use serde::Deserialize;

#[derive(Deserialize)]
pub struct AccountingHourQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8
}

