use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::voltage_level,
    mappers,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<voltage_level::CreateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    repositories::voltage_level::create(&conn, &mappers::voltage_level::create_dto_to_model(&dto))
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn get_all(
    State(pool): State<ConnectionPool>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let results = repositories::voltage_level::read_all(&conn).await.unwrap();
    Ok(Json(results))
}

pub async fn delete(
    State(pool): State<ConnectionPool>,
    id: Path<i32>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::voltage_level::exists(&conn, *id)
        .await
        .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "voltage level not found".to_string()));
    }

    repositories::voltage_level::delete(&conn, *id)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn update(
    State(pool): State<ConnectionPool>,
    id: Path<i32>,
    Json(dto): Json<voltage_level::UpdateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::voltage_level::exists(&conn, *id)
        .await
        .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "voltage level not found".to_string()));
    }

    repositories::voltage_level::update(
        &conn,
        &mappers::voltage_level::update_dto_to_model(&dto),
        *id,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}
