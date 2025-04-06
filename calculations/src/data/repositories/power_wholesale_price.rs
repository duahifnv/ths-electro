use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::PowerWholesalePrice;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    power_wholesale_price: &PowerWholesalePrice,
) -> Result<(), Error> {
    conn
            .execute(
                "INSERT INTO power_wholesale_price (voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month) VALUES ($1, $2, $3, $4, $5, $6, $7)",
                &[&power_wholesale_price.voltage_level_id, &power_wholesale_price.price_category_id, &power_wholesale_price.power_level_id, &power_wholesale_price.contract_type_id, &power_wholesale_price.price, &(power_wholesale_price.year as i32), &(power_wholesale_price.month as i16)],
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
) -> Result<PowerWholesalePrice, Error> {
    let row = conn
            .query_one("SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month FROM power_wholesale_price WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)", 
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],)
            .await?;
    Ok(PowerWholesalePrice {
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
    power_wholesale_price: &PowerWholesalePrice,
    id: i32,
) -> Result<(), Error> {
    conn.execute(
        "UPDATE power_wholesale_price SET price = $1 WHERE id = $2",
        &[&power_wholesale_price.price, &id],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM power_wholesale_price WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists_by_id(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM power_wholesale_price WHERE id = $1)",
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
            "SELECT EXISTS(SELECT 1 FROM power_wholesale_price WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)",
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
