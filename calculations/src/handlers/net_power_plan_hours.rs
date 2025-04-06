use axum::{
    Json,
    extract::{Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::net_power_plan_hours,
    mappers,
    queries::NetPowerPlanHourQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<net_power_plan_hours::Dto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::net_power_plan_hours::exists(&mut conn, dto.year, dto.month)
        .await
        .unwrap();

    if exists {
        return Err((
            StatusCode::NOT_FOUND,
            "this month of this year already exists".to_string(),
        ));
    }

    repositories::net_power_plan_hours::create(
        &mut conn,
        &mappers::net_power_plan_hours::dto_to_model(&dto),
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_all(
    State(pool): State<ConnectionPool>,
    Query(query): Query<NetPowerPlanHourQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::net_power_plan_hours::read_all(&mut conn, query.year, query.month)
        .await
        .unwrap();
    Ok(Json(mappers::net_power_plan_hours::models_to_dto(&results)))
}

pub async fn delete(
    State(pool): State<ConnectionPool>,
    Query(query): Query<NetPowerPlanHourQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::net_power_plan_hours::exists(&mut conn, query.year, query.month)
        .await
        .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "contract type not found".to_string()));
    }
    repositories::net_power_plan_hours::delete(&mut conn, query.year, query.month)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}
