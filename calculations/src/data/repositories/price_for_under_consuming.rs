use tokio_postgres::Error;

use crate::models::PriceForUnderConsuming;

pub struct PriceForUnderConsumingRepository<'a> {
    client: &'a mut tokio_postgres::Client,
}

impl<'a> PriceForUnderConsumingRepository<'a> {
    pub fn new(client: &'a mut tokio_postgres::Client) -> Self {
        PriceForUnderConsumingRepository { client }
    }

    pub async fn create(&mut self, price_for_under_consuming: &PriceForUnderConsuming) -> Result<(), Error> {
        self.client
            .execute(
                "INSERT INTO price_for_under_consuming (voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month) VALUES ($1, $2, $3, $4, $5, $6, $7)",
                &[&price_for_under_consuming.voltage_level_id, &price_for_under_consuming.price_category_id, &price_for_under_consuming.power_level_id, &price_for_under_consuming.contract_type_id, &price_for_under_consuming.price, &(price_for_under_consuming.year as i32), &(price_for_under_consuming.month as i16)],
            )
            .await?;
        Ok(())
    }

    pub async fn read_all(&mut self) -> Result<Vec<PriceForUnderConsuming>, Error> {
        let rows = self
            .client
            .query("SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month FROM price_for_under_consuming", &[])
            .await?;
        let mut results = Vec::new();
        for row in rows {
            results.push(PriceForUnderConsuming {
                id: row.get(0),
                voltage_level_id: row.get(1),
                price_category_id: row.get(2),
                power_level_id: row.get(3),
                contract_type_id: row.get(4),
                price: row.get(5),
                year: row.get::<usize, i64>(6) as u32,
                month: row.get::<usize, i64>(7) as u8,
            });
        }
        Ok(results)
    }

    pub async fn update(&mut self, price_for_under_consuming: &PriceForUnderConsuming) -> Result<(), Error> {
        self.client
            .execute(
                "UPDATE price_for_under_consuming SET price = $1 WHERE id = $2",
                &[
                    &price_for_under_consuming.price,
                    &price_for_under_consuming.id,
                ],
            )
            .await?;
        Ok(())
    }

    pub async fn delete(&mut self, id: i32) -> Result<(), Error> {
        self.client
            .execute("DELETE FROM price_for_under_consuming WHERE id = $1", &[&id])
            .await?;
        Ok(())
    }

    pub async fn exists_by_id(&mut self, id: i32) -> Result<bool, Error> {
        let row = self.client.query_one(
            "SELECT EXISTS(SELECT 1 FROM price_for_under_consuming WHERE id = $1)",
            &[&id],
        ).await?;
        let exists: bool = row.get(0);
        Ok(exists)
    }

    pub async fn exists(
        &mut self,
        voltage_level_id: i32,
        price_category_id: i32,
        power_level_id: i32,
        contract_type_id: i32,
        year: u32,
        month: u8,
    ) -> Result<bool, Error> {
        let row = self.client.query_one(
            "SELECT EXISTS(SELECT 1 FROM price_for_under_consuming WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)",
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
        let exists: bool = row.get(0);
        Ok(exists)
    }
}
