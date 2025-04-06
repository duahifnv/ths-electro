use crate::dtos::accounting_hour;
use crate::models::AccountingHour;

pub fn dto_to_model(dto: &accounting_hour::Dto) -> Vec<AccountingHour> {
    let mut models = Vec::<AccountingHour>::new();
    for unit in dto.units.iter() {
        models.push(AccountingHour {
            id: 0,
            voltage_level_id: dto.voltage_level_id,
            price_category_id: dto.price_category_id,
            power_level_id: dto.power_level_id,
            contract_type_id: dto.contract_type_id,
            year: dto.year,
            month: dto.month,
            day: unit.day,
            hour: unit.hour,
        });
    }

    models
}

pub fn models_to_dto(models: &Vec<AccountingHour>) -> accounting_hour::Dto {
    accounting_hour::Dto {
        voltage_level_id: models[0].voltage_level_id,
        price_category_id: models[0].price_category_id,
        power_level_id: models[0].power_level_id,
        contract_type_id: models[0].contract_type_id,
        year: models[0].year,
        month: models[0].month,
        units: models
            .iter()
            .map(|x| {
                accounting_hour::Unit {
                    day : x.day,
                    hour: x.hour
            }})
            .collect(),
    }
}
