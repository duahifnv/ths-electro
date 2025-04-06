use axum::{Json, extract::State, http::StatusCode, response::IntoResponse};

use crate::{
    calculation::calculate_cost_category_1, data::{dbcontext::ConnectionPool, repositories}, dtos::calculation::{CalculationEasyRequest, CalculationResponse}, mappers
};

use chrono::NaiveDate;

fn days_in_month(year: i32, month: u32) -> Option<u32> {
    if month < 1 || month > 12 {
        return None;
    }

    let date = NaiveDate::from_ymd_opt(year, month, 1)?;
    
    let next_month = if month == 12 {
        NaiveDate::from_ymd_opt(year + 1, 1, 1)
    } else {
        NaiveDate::from_ymd_opt(year, month + 1, 1)
    }?;
    
    Some((next_month - date).num_days() as u32)
}


pub async fn easy_calc(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<CalculationEasyRequest>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let mut response = CalculationResponse {
        one: None,
        two: None,
        three: None,
        four: None,
        five: None,
        six: None
    };

    let one_calc_flag = repositories::fixed_tariff::exists(
        &mut conn,
        dto.voltage_level_id,
        dto.power_level_id,
        dto.contract_type_id,
        dto.year,
        dto.month,
    )
    .await
    .unwrap();

    if one_calc_flag {
        let fixed_tariff = repositories::fixed_tariff::read_by_params(
            &mut conn,
            dto.voltage_level_id,
            dto.power_level_id,
            dto.contract_type_id,
            dto.year,
            dto.month,
        ).await.unwrap();

        response.one = Some(calculate_cost_category_1(fixed_tariff.price, dto.total_power));
    }

    let day_zones_exists = repositories::day_zone::exists(
        &mut conn,
        dto.month,
    )
    .await
    .unwrap();
    

    if day_zones_exists {
        let day_zones = repositories::day_zone::read_all(&mut conn, dto.month).await.unwrap();
        let day_zones = mappers::day_zone::models_to_dto(&day_zones);

        let mut power_left = dto.total_power;
        let mut hour_distribution = Vec::<f64>::new();
        
        day_zones

        if day_zones.units.iter().map(|x| x.hour).len() > 0 {

        }
    }

    Ok(StatusCode::OK)
}
