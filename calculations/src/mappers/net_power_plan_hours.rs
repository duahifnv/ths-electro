use itertools::Itertools;

use crate::dtos::net_power_plan_hours;
use crate::models::NetPowerPlanHours;

pub fn create_dto_to_model(dto: &net_power_plan_hours::Dto) -> Vec<NetPowerPlanHours> {
    let mut models = Vec::<NetPowerPlanHours>::new();
    for unit in dto.units.iter() {
        for hour in unit.hours.iter() {
            models.push(NetPowerPlanHours {
                id: 0,
                year: dto.year,
                month: dto.month,
                day: unit.day,
                hour: *hour,
            });
        }
    }

    models
}

pub fn models_to_dto(models: &Vec<NetPowerPlanHours>) -> net_power_plan_hours::Dto {
    net_power_plan_hours::Dto {
        year: models[0].year,
        month: models[0].month,
        units: models
            .iter()
            .group_by(|x|x.day )
            .into_iter()
            .map(|(day, net_power_plan_hours)| {
                let net_power_plan_hours: Vec<_> = net_power_plan_hours.collect();
                net_power_plan_hours::Unit {
                    day : day,
                    hours: net_power_plan_hours
                    .iter()
                    .map(|x| x.hour)
                    .collect(),
            }})
            .collect(),
    }
}
