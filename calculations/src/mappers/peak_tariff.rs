use crate::dtos::peak_tariff;
use crate::models::PeakTariff;

pub fn create_dto_to_model(dto: &peak_tariff::CreateDto) -> PeakTariff {
    PeakTariff {
        id: 0,
        voltage_level_id: dto.voltage_level_id as i32,
        power_level_id: dto.power_level_id as i32,
        contract_type_id: dto.contract_type_id as i32,
        year: dto.year,
        month: dto.month,
        price: dto.price,
    }
}

pub fn update_dto_to_model(dto: &peak_tariff::UpdateDto) -> PeakTariff {
    PeakTariff {
        id: 0,
        voltage_level_id: 0,
        power_level_id: 0,
        contract_type_id: 0,
        year: 0,
        month: 0,
        price: dto.price,
    }
}