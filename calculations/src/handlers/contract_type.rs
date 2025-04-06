use axum::{
    Json,
    extract::{Path, State},
    http::StatusCode,
    response::IntoResponse,
};

use crate::{
    data::{dbcontext::ConnectionPool, repositories},
    dtos::contract_type,
    mappers,
};

pub async fn create(
    State(pool): State<ConnectionPool>,
    Json(dto): Json<contract_type::CreateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    repositories::contract_type::create(&conn, &mappers::contract_type::create_dto_to_model(&dto))
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

    let results = repositories::contract_type::read_all(&conn).await.unwrap();
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

    let exists = repositories::contract_type::exists(&conn, *id)
        .await
        .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "contract type not found".to_string()));
    }

    repositories::contract_type::delete(&conn, *id)
        .await
        .unwrap();
    Ok(StatusCode::OK)
}

pub async fn update(
    State(pool): State<ConnectionPool>,
    id: Path<i32>,
    Json(dto): Json<contract_type::UpdateDto>,
) -> Result<impl IntoResponse, (StatusCode, String)> {
    let conn = pool
        .get()
        .await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))
        .unwrap();

    let exists = repositories::contract_type::exists(&conn, *id)
        .await
        .unwrap();

    if !exists {
        return Err((StatusCode::NOT_FOUND, "contract type not found".to_string()));
    }

    repositories::contract_type::update(
        &conn,
        &mappers::contract_type::update_dto_to_model(&dto),
        *id,
    )
    .await
    .unwrap();
    Ok(StatusCode::OK)
}
