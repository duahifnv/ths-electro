use axum::{
    Json,
    extract::{Query, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::weekend,
    mappers,
    queries::WeekendQuery,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<weekend::Dto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();
  

    let exists = repositories::weekend::exists(
        &mut conn,
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

    repositories::weekend::create(&mut conn, &mappers::weekend::dto_to_model(&dto))
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_all(
    State(pool): State<ConnectionPool>,
    Query(query): Query<WeekendQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::weekend::read_all(
        &mut conn,
        query.year,
        query.month,
    )
    .await
    .unwrap();
    Ok(Json(mappers::weekend::models_to_dto(&results)))
}

pub async fn delete(
    State(pool): State<ConnectionPool>,
    Query(query): Query<WeekendQuery>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let mut conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::weekend::exists(
        &mut conn,
        query.year,
        query.month,
    )
    .await
    .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "weekend not found".to_string()));
    }
    repositories::weekend::delete(
        &mut conn,
        query.year,
        query.month,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}