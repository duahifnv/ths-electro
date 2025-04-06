use crate::dtos::contract_type;
use crate::models::ContractType;

pub fn create_dto_to_model(dto: &contract_type::CreateDto) -> ContractType {
    ContractType {
        id: 0,
        name: dto.name.to_owned(),
    }
}

pub fn update_dto_to_model(dto: &contract_type::UpdateDto) -> ContractType {
    ContractType {
        id: 0,
        name: dto.name.to_owned(),
    }
}