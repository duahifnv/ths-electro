use itertools::Itertools;

use crate::dtos::hour_tariff::{self, PriceUnit};
use crate::models::HourTariff;

pub fn dto_to_model(dto: &hour_tariff::Dto) -> Vec<HourTariff> {
    let mut models = Vec::<HourTariff>::new();
    for unit in dto.units.iter() {
        for hour_price_unit in unit.hour_price_units.iter() {
            models.push(HourTariff {
                id: 0,
                voltage_level_id: dto.voltage_level_id,
                price_category_id: dto.price_category_id,
                power_level_id: dto.power_level_id,
                contract_type_id: dto.contract_type_id,
                year: dto.year,
                month: dto.month,
                price: hour_price_unit.price,
                day: unit.day,
                hour: hour_price_unit.hour,
            })
        }
    }

    models
}

pub fn models_to_dto(models: &Vec<HourTariff>) -> hour_tariff::Dto {
    hour_tariff::Dto {
        voltage_level_id: models[0].voltage_level_id,
        price_category_id: models[0].price_category_id,
        power_level_id: models[0].power_level_id,
        contract_type_id: models[0].contract_type_id,
        year: models[0].year,
        month: models[0].month,
        units: models
            .iter()
            .group_by(|x| x.day)
            .into_iter()
            .map(|(day, hour_tariffs)| {
                let hour_tariffs: Vec<_> = hour_tariffs.collect();
                hour_tariff::Unit {
                    day: day,
                    hour_price_units: hour_tariffs
                        .iter()
                        .map(|x| PriceUnit {
                            hour: x.hour,
                            price: x.price,
                        })
                        .collect(),
                }
            })
            .collect(),
    }
}
