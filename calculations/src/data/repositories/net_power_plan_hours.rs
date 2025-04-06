use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::NetPowerPlanHours;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    records: &[NetPowerPlanHours],
) -> Result<(), Error> {
    let transaction = conn.transaction().await?;
    for record in records {
        transaction
            .execute(
                "INSERT INTO net_power_plan_hours (year, month, day, hour) VALUES ($1, $2, $3, $4)",
                &[
                    &(record.year as i32),
                    &(record.month as i16),
                    &(record.day as i16),
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
    year: u32,
    month: u8,
) -> Result<Vec<NetPowerPlanHours>, Error> {
    let year = year as i32;
    let month = month as i16;
    let rows = conn
            .query(
                "SELECT id, year, month, day, hour FROM net_power_plan_hours WHERE year= $1 AND month = $2",
                &[&year, &month],
            )
            .await.expect("ababa");
    let mut results = Vec::new();
    for row in rows {
        results.push(NetPowerPlanHours {
            id: row.get(0),
            year: row.get::<usize, i64>(1) as u32,
            month: row.get::<usize, i64>(2) as u8,
            day: row.get::<usize, i64>(3) as u8,
            hour: row.get::<usize, i64>(4) as u8,
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    records: &[NetPowerPlanHours],
) -> Result<(), Error> {
    let transaction = conn.transaction().await?;
    for record in records {
        transaction
                .execute(
                    "UPDATE net_power_plan_hours SET year = $1, month = $2, day = $3, hour = $4 WHERE id = $5",
                    &[
                        &(record.year as i32),
                        &(record.month as i16),
                        &(record.day as i16),
                        &(record.hour as i16),
                        &record.id,
                    ],
                )
                .await?;
    }
    transaction.commit().await?;
    Ok(())
}

pub async fn delete(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    year: u32,
    month: u8,
) -> Result<(), Error> {
    conn.execute(
        "DELETE FROM net_power_plan_hours WHERE AND year = $1 AND month = $2",
        &[&(year as i32), &(month as i16)],
    )
    .await?;
    Ok(())
}

pub async fn exists(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    year: u32,
    month: u8,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM net_power_plan_hours WHERE year = $1 AND month = $2)",
            &[&(year as i32), &(month as i16)],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
