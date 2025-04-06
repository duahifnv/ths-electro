use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::NetPowerPrice;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    net_power_price: &NetPowerPrice,
) -> Result<(), Error> {
    conn
            .execute(
                "INSERT INTO net_power_price (voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month) VALUES ($1, $2, $3, $4, $5, $6, $7)",
                &[&net_power_price.voltage_level_id, &net_power_price.price_category_id, &net_power_price.power_level_id, &net_power_price.contract_type_id, &net_power_price.price, &(net_power_price.year as i32), &(net_power_price.month as i16)],
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
) -> Result<NetPowerPrice, Error> {
    let row = conn
    .query_one("SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month FROM net_power_price WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6", 
    &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)])
    .await?;
    Ok(NetPowerPrice {
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
    net_power_price: &NetPowerPrice,
    id: i32,
) -> Result<(), Error> {
    conn.execute(
        "UPDATE net_power_price SET price = $1 WHERE id = $2",
        &[&net_power_price.price, &id],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM net_power_price WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists_by_id(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM net_power_price WHERE id = $1)",
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
            "SELECT EXISTS(SELECT 1 FROM net_power_price WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)",
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
