use crate::dtos::additional_cost;
use crate::models::AdditionalCost;

pub fn create_dto_to_model(dto: &additional_cost::CreateDto) -> AdditionalCost {
    AdditionalCost {
        id: 0,
        voltage_level_id: dto.voltage_level_id,
        price_category_id: dto.price_category_id,
        power_level_id: dto.power_level_id,
        contract_type_id: dto.contract_type_id,
        year: dto.year,
        month: dto.month,
        price: dto.price,
        name: dto.name.to_owned(),
    }
}

pub fn update_dto_to_model(dto: &additional_cost::UpdateDto) -> AdditionalCost {
    AdditionalCost {
        id: 0,
        voltage_level_id: 0,
        price_category_id: 0,
        power_level_id: 0,
        contract_type_id: 0,
        year: 0,
        month: 0,
        price: dto.price,
        name: dto.name.to_owned(),
    }
}