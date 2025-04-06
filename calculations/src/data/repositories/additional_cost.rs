use bb8::PooledConnection;
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::{Error, NoTls};

use crate::models::AdditionalCost;

pub async fn create(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    additional_cost: &AdditionalCost,
) -> Result<(), Error> {
    conn
            .execute(
                "INSERT INTO additional_cost (voltage_level_id, price_category_id, power_level_id, contract_type_id, year, month, price, name) VALUES ($1, $2, $3, $4, $5, $6, $7, $8)",
                &[&additional_cost.voltage_level_id, &additional_cost.price_category_id, &additional_cost.power_level_id, &additional_cost.contract_type_id, &(additional_cost.year as i32), &(additional_cost.month as i16), &additional_cost.price, &additional_cost.name],
            )
            .await?;
    Ok(())
}

pub async fn read_all(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
) -> Result<Vec<AdditionalCost>, Error> {
    let rows = conn
            .query("SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, year, month, price, name FROM additional_cost", &[])
            .await?;
    let mut results = Vec::new();
    for row in rows {
        results.push(AdditionalCost {
            id: row.get(0),
            voltage_level_id: row.get(1),
            price_category_id: row.get(2),
            power_level_id: row.get(3),
            contract_type_id: row.get(4),
            year: row.get::<usize, i64>(5) as u32,
            month: row.get::<usize, i64>(6) as u8,
            price: row.get(7),
            name: row.get(8),
        });
    }
    Ok(results)
}

pub async fn read_all_by_params(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    voltage_level_id: i32,
    price_category_id: i32,
    power_level_id: i32,
    contract_type_id: i32,
    year: u32,
    month: u8,
) -> Result<Vec<AdditionalCost>, Error> {
    let rows = conn
    .query("SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, year, month, price, name FROM additional_cost WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6", 
    &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)])
    .await?;
    let mut results = Vec::new();
    for row in rows {
        results.push(AdditionalCost {
            id: row.get(0),
            voltage_level_id: row.get(1),
            price_category_id: row.get(2),
            power_level_id: row.get(3),
            contract_type_id: row.get(4),
            year: row.get::<usize, i64>(5) as u32,
            month: row.get::<usize, i64>(6) as u8,
            price: row.get(7),
            name: row.get(8),
        });
    }
    Ok(results)
}

pub async fn update(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    additional_cost: &AdditionalCost,
    id: i32,
) -> Result<(), Error> {
    conn.execute(
        "UPDATE additional_cost SET name = $1, price = $2 WHERE id = $3",
        &[&additional_cost.name, &additional_cost.price, &id],
    )
    .await?;
    Ok(())
}

pub async fn delete(
    conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>,
    id: i32,
) -> Result<(), Error> {
    conn.execute("DELETE FROM additional_cost WHERE id = $1", &[&id])
        .await?;
    Ok(())
}

pub async fn exists_by_id(conn: &mut PooledConnection<'_, PostgresConnectionManager<NoTls>>, id: i32) -> Result<bool, Error> {
    let row = conn
        .query_one(
            "SELECT EXISTS(SELECT 1 FROM additional_cost WHERE id = $1)",
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
            "SELECT EXISTS(SELECT 1 FROM additional_cost WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)",
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
    let exists: bool = row.get(0);
    Ok(exists)
}
