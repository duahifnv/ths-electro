use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::HourTariff;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    records: &[HourTariff],
) -> Result<(), Error> {
    let transaction = conn.transaction().await?;
    for record in records {
        transaction
                .execute(
                    "INSERT INTO hour_tariff (voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month, day, hour) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)",
                    &[
                        &record.voltage_level_id,
                        &record.price_category_id,
                        &record.power_level_id,
                        &record.contract_type_id,
                        &record.price,
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
    voltage_level_id: i32,
    price_category_id: i32,
    power_level_id: i32,
    contract_type_id: i32,
    year: u32,
    month: u8,
) -> Result<Vec<HourTariff>, Error> {
    let year = year as i32;
    let month = month as i16;
    let rows = conn
            .query(
                "SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month, day, hour FROM hour_tariff WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6",
                &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &year, &month],
            )
            .await.expect("ababa");
    let mut results = Vec::new();
    for row in rows {
        results.push(HourTariff {
            id: row.get(0),
            voltage_level_id: row.get(1),
            price_category_id: row.get(2),
            power_level_id: row.get(3),
            contract_type_id: row.get(4),
            price: row.get(5),
            year: row.get::<usize, i64>(6) as u32,
            month: row.get::<usize, i64>(7) as u8,
            day: row.get::<usize, i64>(8) as u8,
            hour: row.get::<usize, i64>(9) as u8,
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    records: &[HourTariff],
) -> Result<(), Error> {
    let transaction = conn.transaction().await?;
    for record in records {
        transaction
                .execute(
                    "UPDATE hour_tariff SET voltage_level_id = $1, price_category_id = $2, power_level_id = $3, contract_type_id = $4, price = $5, year = $6, month = $7, day = $8, hour = $9 WHERE id = $10",
                    &[
                        &record.voltage_level_id,
                        &record.price_category_id,
                        &record.power_level_id,
                        &record.contract_type_id,
                        &record.price,
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
    voltage_level_id: i32,
    price_category_id: i32,
    power_level_id: i32,
    contract_type_id: i32,
    year: u32,
    month: u8,
) -> Result<(), Error> {
    conn
            .execute(
                "DELETE FROM hour_tariff WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6",
                &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
            )
            .await?;
    Ok(())
}

pub async fn exists(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    voltage_level_id: i32,
    price_category_id: i32,
    power_level_id: i32,
    contract_type_id: i32,
    year: u32,
    month: u8,
) -> Result<bool, Error> {
    let row = conn.query_one(
            "SELECT EXISTS(SELECT 1 FROM hour_tariff WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)",
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
