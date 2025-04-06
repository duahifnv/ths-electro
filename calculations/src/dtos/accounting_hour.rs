use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct Unit {
    pub day: u8,
    pub hour: u8
}

#[derive(Serialize, Deserialize)]
pub struct Dto {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
    pub units: Vec<Unit>,
}