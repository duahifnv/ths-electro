use crate::dtos::fixed_tariff;
use crate::models::FixedTariff;

pub fn create_dto_to_model(dto: &fixed_tariff::CreateDto) -> FixedTariff {
    FixedTariff {
        id: 0,
        voltage_level_id: dto.voltage_level_id,
        power_level_id: dto.power_level_id,
        contract_type_id: dto.contract_type_id,
        year: dto.year,
        month: dto.month,
        price: dto.price,
    }
}

pub fn update_dto_to_model(dto: &fixed_tariff::UpdateDto) -> FixedTariff {
    FixedTariff {
        id: 0,
        voltage_level_id: 0,
        power_level_id: 0,
        contract_type_id: 0,
        year: 0,
        month: 0,
        price: dto.price,
    }
}