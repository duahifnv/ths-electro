use crate::dtos::day_zone;
use crate::models::DayZone;

pub fn dto_to_model(dto: &day_zone::Dto) -> Vec<DayZone> {
    let mut models = Vec::<DayZone>::new();
    for unit in dto.units.iter() {
        models.push(DayZone {
            id: 0,
            month: dto.month,
            hour: unit.hour,
            zone_type: unit.zone_type,
        });
    }

    models
}

pub fn models_to_dto(models: &Vec<DayZone>) -> day_zone::Dto {
    day_zone::Dto {
        month: models[0].month,
        units: models
            .iter()
            .map(|x| day_zone::Unit {
                hour: x.hour,
                zone_type: x.zone_type,
            })
            .collect(),
    }
}
