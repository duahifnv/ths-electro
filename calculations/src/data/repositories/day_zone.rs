use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::DayZone;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    records: &[DayZone],
) -> Result<(), Error> {
    let transaction = conn.transaction().await?;
    for record in records {
        transaction
            .execute(
                "INSERT INTO day_zone (zone_type, month, hour) VALUES ($1, $2, $3)",
                &[
                    &(record.zone_type as i16),
                    &(record.month as i16),
                    &(record.hour as i16),
                ],
            )
            .await?;
    }
    transaction.commit().await?;
    Ok(())
}

pub async fn read_all(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    month: u8,
) -> Result<Vec<DayZone>, Error> {
    let month = month as i16;
    let rows = conn
        .query(
            "SELECT id, zone_type, month, day FROM day_zone WHERE month = $1",
            &[&month],
        )
        .await?;
    let mut results = Vec::new();
    for row in rows {
        results.push(DayZone {
            id: row.get(0),
            zone_type: row.get::<usize, i64>(1) as u8,
            month: row.get::<usize, i64>(2) as u8,
            hour: row.get::<usize, i64>(3) as u8,
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    records: &[DayZone],
) -> Result<(), Error> {
    let transaction = conn.transaction().await?;
    for record in records {
        transaction
            .execute(
                "UPDATE zone_type SET zone_type = $1, WHERE id = $2",
                &[&(record.zone_type as i16), &record.id],
            )
            .await?;
    }
    transaction.commit().await?;
    Ok(())
}

pub async fn delete(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    month: u8,
) -> Result<(), Error> {
    conn.execute(
        "DELETE FROM zone_type WHERE AND month = $1",
        &[&(month as i16)],
    )
    .await?;
    Ok(())
}

pub async fn exists(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    month: u8,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM day_zone WHERE month = $1)",
            &[&(month as i16)],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
