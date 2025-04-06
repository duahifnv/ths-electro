use crate::dtos::weekend;
use crate::models::Weekend;

pub fn dto_to_model(dto: &weekend::Dto) -> Vec<Weekend> {
    let mut models = Vec::<Weekend>::new();
    for day in dto.days.iter() {
        models.push(Weekend {
            id: 0,
            year: dto.year,
            month: dto.month,
            day: *day,
        });
    }

    models
}

pub fn models_to_dto(models: &Vec<Weekend>) -> weekend::Dto {
    weekend::Dto {
        year: models[0].year,
        month: models[0].month,
        days: models
            .iter()
            .map(|x| x.day)
            .collect(),
    }
}
