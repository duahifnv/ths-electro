use crate::dtos::price_category;
use crate::models::PriceCategory;

pub fn create_dto_to_model(dto: &price_category::CreateDto) -> PriceCategory {
    PriceCategory {
        id: 0,
        name: dto.name.to_owned(),
    }
}

pub fn update_dto_to_model(dto: &price_category::UpdateDto) -> PriceCategory {
    PriceCategory {
        id: 0,
        name: dto.name.to_owned(),
    }
}