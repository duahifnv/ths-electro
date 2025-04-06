use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};


use crate::models::PowerLevel;

pub async fn create(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    power_level: &PowerLevel,
) -> Result<(), Error> {
    conn
        .execute(
            "INSERT INTO power_level (name) VALUES ($1)",
            &[&power_level.name],
        )
        .await?;
    Ok(())
}

pub async fn read_all(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
) -> Result<Vec<PowerLevel>, Error> {
    let rows = conn
        .query("SELECT id, name FROM power_level", &[])
        .await?;
    let mut results = Vec::new();
    for row in rows {
        results.push(PowerLevel {
            id: row.get(0),
            name: row.get(1),
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    power_level: &PowerLevel,
    id: i32,
) -> Result<(), Error> {
    conn
        .execute(
            "UPDATE power_level SET name = $1 WHERE id = $2",
            &[&power_level.name, &id],
        )
        .await?;
    Ok(())
}

pub async fn delete(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn
        .execute("DELETE FROM power_level WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM power_level WHERE id = $1)",
            &[&id],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
