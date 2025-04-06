use crate::dtos::voltage_level;
use crate::models::VoltageLevel;

pub fn create_dto_to_model(dto: &voltage_level::CreateDto) -> VoltageLevel {
    VoltageLevel {
        id: 0,
        name: dto.name.to_owned(),
    }
}

pub fn update_dto_to_model(dto: &voltage_level::UpdateDto) -> VoltageLevel {
    VoltageLevel {
        id: 0,
        name: dto.name.to_owned(),
    }
}