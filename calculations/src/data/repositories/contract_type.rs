use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::ContractType;

pub async fn create(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    contract_type: &ContractType,
) -> Result<(), Error> {
    conn.execute(
        "INSERT INTO contract_type (name) VALUES ($1)",
        &[&contract_type.name],
    )
    .await?;
    Ok(())
}

pub async fn read_all(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
) -> Result<Vec<ContractType>, Error> {
    let rows = conn
        .query("SELECT id, name FROM contract_type", &[])
        .await?;
    let mut results = Vec::new();
    for row in rows {
        results.push(ContractType {
            id: row.get(0),
            name: row.get(1),
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    contract_type: &ContractType,
    id: i32,
) -> Result<(), Error> {
    conn.execute(
        "UPDATE contract_type SET name = $1 WHERE id = $2",
        &[&contract_type.name, &id],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM contract_type WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM contract_type WHERE id = $1)",
            &[&id],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
