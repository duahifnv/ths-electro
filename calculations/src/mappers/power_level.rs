use crate::dtos::power_level;
use crate::models::PowerLevel;

pub fn create_dto_to_model(dto: &power_level::CreateDto) -> PowerLevel {
    PowerLevel {
        id: 0,
        name: dto.name.to_owned(),
    }
}

pub fn update_dto_to_model(dto: &power_level::UpdateDto) -> PowerLevel {
    PowerLevel {
        id: 0,
        name: dto.name.to_owned(),
    }
}