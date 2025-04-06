use serde::Deserialize;

#[derive(Deserialize)]
pub struct AccountingHourQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct AditionalCostQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct DayZoneQuery {
    pub month: u8,
}

#[derive(Deserialize)]
pub struct FixedTariffQuery {
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct HalfPeakTariffQuery {
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct HourTariffQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct NetPowerPlanHourQuery {
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct NetPowerPriceQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct OffPeakTariffQuery {
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct PeakTariffQuery {
    pub voltage_level_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct PowerWholesalePriceQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct PriceForOverConsumingQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct PriceForUnderConsumingQuery {
    pub voltage_level_id: i32,
    pub price_category_id: i32,
    pub power_level_id: i32,
    pub contract_type_id: i32,
    pub year: u32,
    pub month: u8,
}

#[derive(Deserialize)]
pub struct WeekendQuery {
    pub year: u32,
    pub month: u8,
}