#[derive(serde::Serialize)]
pub struct VoltageLevel {
    pub id: i32,
    pub name: String,
}
#[derive(serde::Serialize)]
pub struct PowerLevel {
    pub id: i32,
    pub name: String
}
#[derive(serde::Serialize)]
pub struct ContractType {
    pub id: i32,
    pub name: String
}
#[derive(serde::Serialize)]
pub struct FixedTariff {
    pub id: i32,
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub price: f64,
    pub year: u32,
    pub month: u8,
}
pub struct DayZone {
    pub id: i32,
    pub zone_type: u8,
    pub month: u8,
    pub hour: u8,
}
#[derive(serde::Serialize)]
pub struct OffPeakTariff {
    pub id: i32,
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub price: f64,
    pub year: u32,
    pub month: u8,
}
#[derive(serde::Serialize)]
pub struct HalfPeakTariff {
    pub id: i32,
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub price: f64,
    pub year: u32,
    pub month: u8,
}
#[derive(serde::Serialize)]
pub struct PeakTariff {
    pub id: i32,
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub price: f64,
    pub year: u32,
    pub month: u8,
}
#[derive(serde::Serialize)]
pub struct PriceCategory {
    pub id: i32,
    pub name: String
}
#[derive(std::fmt::Debug)]
pub struct HourTariff {
    pub id: i32,
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub price: f64,
    pub year: u32,
    pub month: u8,
    pub day: u8,
    pub hour: u8
}

pub struct AccountingHour {
    pub id: i32,
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
    pub day: u8,
    pub hour: u8
}
#[derive(serde::Serialize)]
pub struct PowerWholesalePrice {
    pub id: i32,
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub price: f64,
    pub year: u32,
    pub month: u8,
}

pub struct NetPowerPlanHours {
    pub id: i32,
    pub year: u32,
    pub month: u8,
    pub day: u8,
    pub hour: u8,
}
#[derive(serde::Serialize)]
pub struct NetPowerPrice {
    pub id: i32,
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
    pub price: f64,
}
#[derive(serde::Serialize)]
pub struct PriceForUnderConsuming {
    pub id: i32,
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
    pub price: f64,
}
#[derive(serde::Serialize)]
pub struct PriceForOverConsuming {
    pub id: i32,
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
    pub price: f64,
}

#[derive(serde::Serialize)]
pub struct AdditionalCost {
    pub id: i32,
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
    pub price: f64,
    pub name: String
}

pub struct Weekend {
    pub id: i32,
    pub year: u32,
    pub month: u8,
    pub day: u8,
}