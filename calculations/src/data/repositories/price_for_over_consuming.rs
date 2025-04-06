use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::PriceForOverConsuming;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    price_for_over_consuming: &PriceForOverConsuming,
) -> Result<(), Error> {
    conn
            .execute(
                "INSERT INTO price_for_over_consuming (voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month) VALUES ($1, $2, $3, $4, $5, $6, $7)",
                &[&price_for_over_consuming.voltage_level_id, &price_for_over_consuming.price_category_id, &price_for_over_consuming.power_level_id, &price_for_over_consuming.contract_type_id, &price_for_over_consuming.price, &(price_for_over_consuming.year as i32), &(price_for_over_consuming.month as i16)],
            )
            .await?;
    Ok(())
}

pub async fn read_by_params(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    voltage_level_id: i32,
    price_category_id: i32,
    power_level_id: i32,
    contract_type_id: i32,
    year: u32,
    month: u8,
) -> Result<PriceForOverConsuming, Error> {
    let row = conn
            .query_one("SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month FROM price_for_over_consuming WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)", &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],)
            .await?;
    Ok(PriceForOverConsuming {
        id: row.get(0),
        voltage_level_id: row.get(1),
        price_category_id: row.get(2),
        power_level_id: row.get(3),
        contract_type_id: row.get(4),
        price: row.get(5),
        year: row.get::<usize, i64>(6) as u32,
        month: row.get::<usize, i64>(7) as u8,
    })
}

pub async fn update(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    price_for_over_consuming: &PriceForOverConsuming, id: i32
) -> Result<(), Error> {
    conn.execute(
        "UPDATE price_for_over_consuming SET price = $1 WHERE id = $2",
        &[
            &price_for_over_consuming.price,
            &id,
        ],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM price_for_over_consuming WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists_by_id(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM price_for_over_consuming WHERE id = $1)",
            &[&id],
        )
        .await?;
    let exists: bool = row.get(0);
    Ok(exists)
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
            "SELECT EXISTS(SELECT 1 FROM price_for_over_consuming WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)",
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
