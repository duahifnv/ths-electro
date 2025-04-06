use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::price_category,
    mappers,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<price_category::CreateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    repositories::price_category::create(&conn, &mappers::price_category::create_dto_to_model(&dto))
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

    let results = repositories::price_category::read_all(&conn).await.unwrap();
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

    let exists = repositories::price_category::exists(&conn, *id)
        .await
        .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "price category not found".to_string()));
    }

    repositories::price_category::delete(&conn, *id)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn update(
    State(pool): State<ConnectionPool>,
    id: Path<i32>,
    Json(dto): Json<price_category::UpdateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::price_category::exists(&conn, *id)
        .await
        .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "price category not found".to_string()));
    }

    repositories::price_category::update(
        &conn,
        &mappers::price_category::update_dto_to_model(&dto),
        *id,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}
