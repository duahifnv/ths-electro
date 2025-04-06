use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::PeakTariff;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    peak_tariff: &PeakTariff,
) -> Result<(), Error> {
    conn
            .execute(
                "INSERT INTO peak_tariff (voltage_level_id, power_level_id, contract_type_id, price, year, month) VALUES ($1, $2, $3, $4, $5, $6)",
                &[&peak_tariff.voltage_level_id, &peak_tariff.power_level_id, &peak_tariff.contract_type_id, &peak_tariff.price, &(peak_tariff.year as i32), &(peak_tariff.month as i16)],
            )
            .await?;
    Ok(())
}

pub async fn read_by_params(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    voltage_level_id: i32,
    power_level_id: i32,
    contract_type_id: i32,
    year: u32,
    month: u8,
) -> Result<PeakTariff, Error> {
    let row = conn
            .query_one("SELECT id, voltage_level_id, power_level_id, contract_type_id, price, year, month FROM peak_tariff WHERE voltage_level_id = $1 AND power_level_id = $2 AND contract_type_id = $3 AND year = $4 AND month = $5", 
    &[&voltage_level_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)])
            .await?;
    Ok(PeakTariff {
        id: row.get(0),
        voltage_level_id: row.get(1),
        power_level_id: row.get(2),
        contract_type_id: row.get(3),
        price: row.get(4),
        year: row.get::<usize, i64>(5) as u32,
        month: row.get::<usize, i64>(6) as u8,
    })
}

pub async fn update(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    peak_tariff: &PeakTariff,
    id: i32,
) -> Result<(), Error> {
    conn.execute(
        "UPDATE peak_tariff SET price = $1 WHERE id = $2",
        &[&peak_tariff.price, &id],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM peak_tariff WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists_by_id(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM peak_tariff WHERE id = $1)",
            &[&id],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
}

pub async fn exists(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    voltage_level_id: i32,
    power_level_id: i32,
    contract_type_id: i32,
    year: u32,
    month: u8,
) -> Result<bool, Error> {
    let row = conn.query_one(
            "SELECT EXISTS(SELECT 1 FROM peak_tariff WHERE voltage_level_id = $1 AND power_level_id = $2 AND contract_type_id = $3 AND year = $4 AND month = $5)",
            &[&voltage_level_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
