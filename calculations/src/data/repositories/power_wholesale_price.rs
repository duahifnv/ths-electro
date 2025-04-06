use tokio_postgres::Error;

use crate::models::PowerWholesalePrice;

pub struct PowerWholesalePriceRepository<'a> {
    client: &'a mut tokio_postgres::Client,
}

impl<'a> PowerWholesalePriceRepository<'a> {
    pub fn new(client: &'a mut tokio_postgres::Client) -> Self {
        PowerWholesalePriceRepository { client }
    }

    pub async fn create(&mut self, power_wholesale_price: &PowerWholesalePrice) -> Result<(), Error> {
        self.client
            .execute(
                "INSERT INTO power_wholesale_price (voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month) VALUES ($1, $2, $3, $4, $5, $6, $7)",
                &[&power_wholesale_price.voltage_level_id, &power_wholesale_price.price_category_id, &power_wholesale_price.power_level_id, &power_wholesale_price.contract_type_id, &power_wholesale_price.price, &(power_wholesale_price.year as i32), &(power_wholesale_price.month as i16)],
            )
            .await?;
        Ok(())
    }

    pub async fn read_all(&mut self) -> Result<Vec<PowerWholesalePrice>, Error> {
        let rows = self
            .client
            .query("SELECT id, voltage_level_id, price_category_id, power_level_id, contract_type_id, price, year, month FROM power_wholesale_price", &[])
            .await?;
        let mut results = Vec::new();
        for row in rows {
            results.push(PowerWholesalePrice {
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

    pub async fn update(&mut self, power_wholesale_price: &PowerWholesalePrice) -> Result<(), Error> {
        self.client
            .execute(
                "UPDATE power_wholesale_price SET price = $1 WHERE id = $2",
                &[
                    &power_wholesale_price.price,
                    &power_wholesale_price.id,
                ],
            )
            .await?;
        Ok(())
    }

    pub async fn delete(&mut self, id: i32) -> Result<(), Error> {
        self.client
            .execute("DELETE FROM power_wholesale_price WHERE id = $1", &[&id])
            .await?;
        Ok(())
    }

    pub async fn exists_by_id(&mut self, id: i32) -> Result<bool, Error> {
        let row = self.client.query_one(
            "SELECT EXISTS(SELECT 1 FROM power_wholesale_price WHERE id = $1)",
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
            "SELECT EXISTS(SELECT 1 FROM power_wholesale_price WHERE voltage_level_id = $1 AND price_category_id = $2 AND power_level_id = $3 AND contract_type_id = $4 AND year = $5 AND month = $6)",
            &[&voltage_level_id, &price_category_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
        let exists: bool = row.get(0);
        Ok(exists)
    }
}
