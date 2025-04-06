use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::VoltageLevel;

pub async fn create(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    voltage_level: &VoltageLevel,
) -> Result<(), Error> {
    conn.execute(
        "INSERT INTO voltage_level (name) VALUES ($1)",
        &[&voltage_level.name],
    )
    .await?;
    Ok(())
}

pub async fn read_all(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
) -> Result<Vec<VoltageLevel>, Error> {
    let rows = conn
        .query("SELECT id, name FROM voltage_level", &[])
        .await?;
    let mut results = Vec::new();
    for row in rows {
        results.push(VoltageLevel {
            id: row.get(0),
            name: row.get(1),
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    voltage_level: &VoltageLevel,
    id: i32,
) -> Result<(), Error> {
    conn.execute(
        "UPDATE voltage_level SET name = $1 WHERE id = $2",
        &[&voltage_level.name, &id],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM voltage_level WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM voltage_level WHERE id = $1)",
            &[&id],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
