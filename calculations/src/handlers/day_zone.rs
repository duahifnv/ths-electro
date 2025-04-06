use axum::{
    Json,
    extract::{Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::day_zone,
    mappers,
    queries::DayZoneQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<day_zone::Dto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::day_zone::exists(&mut conn, dto.month)
        .await
        .unwrap();

    if exists {
        return Err((
            StatusCode::BAD_REQUEST,
            "this month already exists".to_string(),
        ));
    }

    repositories::day_zone::create(&mut conn, &mappers::day_zone::dto_to_model(&dto))
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_all(
    State(pool): State<ConnectionPool>,
    Query(query): Query<DayZoneQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::day_zone::read_all(&mut conn, query.month)
        .await
        .unwrap();
    Ok(Json(mappers::day_zone::models_to_dto(&results)))
}

pub async fn delete(
    State(pool): State<ConnectionPool>,
    Query(query): Query<DayZoneQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::day_zone::exists(&mut conn, query.month)
        .await
        .unwrap();

    if exists {
        return Err((
            StatusCode::NOT_FOUND,
            "day zones for this month found".to_string(),
        ));
    }

    repositories::day_zone::delete(&mut conn, query.month)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

