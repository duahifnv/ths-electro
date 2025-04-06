use axum::{
    Json,
    extract::{Path, Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::price_for_over_consuming,
    mappers,
    queries::PriceForOverConsumingQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<price_for_over_consuming::CreateDto>,
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

    repositories::price_for_over_consuming::create(
        &mut conn,
        &mappers::price_for_over_consuming::create_dto_to_model(&dto),
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_by_params(
    State(pool): State<ConnectionPool>,
    Query(query): Query<PriceForOverConsumingQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::price_for_over_consuming::read_by_params(
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

    let exists = repositories::price_for_over_consuming::exists_by_id(&mut conn, id)
        .await
        .unwrap();
    if !exists {
        return Err((
            StatusCode::NOT_FOUND,
            "additional cost not found".to_string(),
        ));
    }

    repositories::price_for_over_consuming::delete(&mut conn, id)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn update(
    State(pool): State<ConnectionPool>,
    Path(id): Path<i32>,
    Json(dto): Json<price_for_over_consuming::UpdateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::price_for_over_consuming::exists_by_id(&mut conn, id)
        .await
        .unwrap();
    if !exists {
        return Err((
            StatusCode::NOT_FOUND,
            "additional cost not found".to_string(),
        ));
    }

    repositories::price_for_over_consuming::update(
        &mut conn,
        &mappers::price_for_over_consuming::update_dto_to_model(&dto),
        id,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}
