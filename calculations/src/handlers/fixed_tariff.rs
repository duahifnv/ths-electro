use axum::{
    Json,
    extract::{Path, Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::fixed_tariff,
    mappers,
    queries::FixedTariffQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<fixed_tariff::CreateDto>,
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

    let exists = repositories::fixed_tariff::exists(
        &mut conn,
        dto.voltage_level_id,
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
            "tariff for this month of this year already exists".to_string(),
        ));
    }

    repositories::fixed_tariff::create(
        &mut conn,
        &mappers::fixed_tariff::create_dto_to_model(&dto),
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_by_params(
    State(pool): State<ConnectionPool>,
    Query(query): Query<FixedTariffQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::fixed_tariff::read_by_params(
        &mut conn,
        query.voltage_level_id,
        query.power_level_id,
        query.contract_type_id,
        query.year,
        query.month,
    )
    .await
    .unwrap();
    Ok(Json(results))
}

pub async fn delete(
    State(pool): State<ConnectionPool>,
    Path(id): Path<i32>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::fixed_tariff::exists_by_id(&mut conn, id)
        .await
        .unwrap();
    if !exists {
        return Err((
            StatusCode::NOT_FOUND,
            "fixed tariff not found".to_string(),
        ));
    }

    repositories::fixed_tariff::delete(&mut conn, id)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn update(
    State(pool): State<ConnectionPool>,
    Path(id): Path<i32>,
    Json(dto): Json<fixed_tariff::UpdateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::fixed_tariff::exists_by_id(&mut conn, id)
        .await
        .unwrap();
    if !exists {
        return Err((
            StatusCode::NOT_FOUND,
            "fixed tariff not found".to_string(),
        ));
    }

    repositories::fixed_tariff::update(
        &mut conn,
        &mappers::fixed_tariff::update_dto_to_model(&dto),
        id,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}
