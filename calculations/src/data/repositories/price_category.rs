use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::PriceCategory;

pub async fn create(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    price_category: &PriceCategory,
) -> Result<(), Error> {
    conn.execute(
        "INSERT INTO price_category (name) VALUES ($1)",
        &[&price_category.name],
    )
    .await?;
    Ok(())
}

pub async fn read_all(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
) -> Result<Vec<PriceCategory>, Error> {
    let rows = conn
        .query("SELECT id, name FROM price_category", &[])
        .await?;
    let mut results = Vec::new();
    for row in rows {
        results.push(PriceCategory {
            id: row.get(0),
            name: row.get(1),
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    price_category: &PriceCategory,
    id: i32,
) -> Result<(), Error> {
    conn.execute(
        "UPDATE price_category SET name = $1 WHERE id = $2",
        &[&price_category.name, &id],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM price_category WHERE id = $1", &[&id])
        .await?;
    Ok(())
}
pub async fn exists(
    conn: &PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM price_category WHERE id = $1)",
            &[&id],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
