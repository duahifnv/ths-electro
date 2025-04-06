use axum::{
    Json,
    extract::{Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::hour_tariff,
    mappers,
    queries::HourTariffQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<hour_tariff::Dto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let voltage_level_exists = repositories::voltage_level::exists(&conn, dto.voltage_level_id)
        .await
        .unwrap();

    if !voltage_level_exists {
        return Err((StatusCode::NOT_FOUND, "voltage level not found".to_string()));
    }

    let price_category_exists = repositories::price_category::exists(&conn, dto.price_category_id)
        .await
        .unwrap();

    if !price_category_exists {
        return Err((
            StatusCode::NOT_FOUND,
            "price category not found".to_string(),
        ));
    }

    let power_level_exists = repositories::power_level::exists(&conn, dto.power_level_id)
        .await
        .unwrap();

    if !power_level_exists {
        return Err((StatusCode::NOT_FOUND, "power level not found".to_string()));
    }

    let contract_type_exists = repositories::voltage_level::exists(&conn, dto.contract_type_id)
        .await
        .unwrap();

    if !contract_type_exists {
        return Err((StatusCode::NOT_FOUND, "contract type not found".to_string()));
    }

    let exists = repositories::hour_tariff::exists(
        &mut conn,
        dto.voltage_level_id,
        dto.price_category_id,
        dto.power_level_id,
        dto.contract_type_id,
        dto.year,
        dto.month,
    )
    .await
    .unwrap();

    if exists {
        return Err((
            StatusCode::NOT_FOUND,
            "this month of this year already exists".to_string(),
        ));
    }

    repositories::hour_tariff::create(&mut conn, &mappers::hour_tariff::dto_to_model(&dto))
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_all(
    State(pool): State<ConnectionPool>,
    Query(query): Query<HourTariffQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::hour_tariff::read_all(
        &mut conn,
        query.voltage_level_id,
        query.price_category_id,
        query.power_level_id,
        query.contract_type_id,
        query.year,
        query.month,
    )
    .await
    .unwrap();
    Ok(Json(mappers::hour_tariff::models_to_dto(&results)))
}

pub async fn delete(
    State(pool): State<ConnectionPool>,
    Query(query): Query<HourTariffQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::hour_tariff::exists(
        &mut conn,
        query.voltage_level_id,
        query.price_category_id,
        query.power_level_id,
        query.contract_type_id,
        query.year,
        query.month,
    )
    .await
    .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "contract type not found".to_string()));
    }
    repositories::hour_tariff::delete(
        &mut conn,
        query.voltage_level_id,
        query.price_category_id,
        query.power_level_id,
        query.contract_type_id,
        query.year,
        query.month,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}