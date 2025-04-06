use axum::{
    Json,
    extract::{Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::accounting_hour,
    mappers,
    queries::AccountingHourQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<accounting_hour::Dto>,
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

    let exists = repositories::accounting_hour::exists(
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

    repositories::accounting_hour::create(
        &mut conn,
        &mappers::accounting_hour::create_dto_to_model(&dto),
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_all(
    State(pool): State<ConnectionPool>,
    Query(query): Query<AccountingHourQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::accounting_hour::read_all(
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
    Ok(Json(mappers::accounting_hour::models_to_dto(&results)))
}

pub async fn delete(
    State(pool): State<ConnectionPool>,
    Query(query): Query<AccountingHourQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let voltage_level_exists = repositories::voltage_level::exists(&conn, query.voltage_level_id)
        .await
        .unwrap();

    if !voltage_level_exists {
        return Err((StatusCode::NOT_FOUND, "voltage level not found".to_string()));
    }

    let price_category_exists =
        repositories::price_category::exists(&conn, query.price_category_id)
            .await
            .unwrap();

    if !price_category_exists {
        return Err((
            StatusCode::NOT_FOUND,
            "price category not found".to_string(),
        ));
    }

    let power_level_exists = repositories::power_level::exists(&conn, query.power_level_id)
        .await
        .unwrap();

    if !power_level_exists {
        return Err((StatusCode::NOT_FOUND, "power level not found".to_string()));
    }

    let contract_type_exists = repositories::voltage_level::exists(&conn, query.contract_type_id)
        .await
        .unwrap();

    if !contract_type_exists {
        return Err((StatusCode::NOT_FOUND, "contract type not found".to_string()));
    }
    repositories::accounting_hour::delete(
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

pub async fn update(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<accounting_hour::Dto>,
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

    repositories::accounting_hour::update(
        &mut conn,
        &&mappers::accounting_hour::create_dto_to_model(&dto),
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}
