use axum::{
    Json,
    extract::{Path, Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::power_wholesale_price,
    mappers,
    queries::PowerWholesalePriceQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<power_wholesale_price::CreateDto>,
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

    repositories::power_wholesale_price::create(
        &mut conn,
        &mappers::power_wholesale_price::create_dto_to_model(&dto),
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_by_params(
    State(pool): State<ConnectionPool>,
    Query(query): Query<PowerWholesalePriceQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::power_wholesale_price::read_by_params(
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

    let exists = repositories::power_wholesale_price::exists_by_id(&mut conn, id)
        .await
        .unwrap();
    if !exists {
        return Err((
            StatusCode::NOT_FOUND,
            "additional cost not found".to_string(),
        ));
    }

    repositories::power_wholesale_price::delete(&mut conn, id)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn update(
    State(pool): State<ConnectionPool>,
    Path(id): Path<i32>,
    Json(dto): Json<power_wholesale_price::UpdateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::power_wholesale_price::exists_by_id(&mut conn, id)
        .await
        .unwrap();
    if !exists {
        return Err((
            StatusCode::NOT_FOUND,
            "additional cost not found".to_string(),
        ));
    }

    repositories::power_wholesale_price::update(
        &mut conn,
        &mappers::power_wholesale_price::update_dto_to_model(&dto),
        id,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}
