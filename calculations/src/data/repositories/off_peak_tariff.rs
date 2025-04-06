use tokio_postgres::Error;

use crate::models::OffPeakTariff;

pub struct OffPeakTariffRepository {
    client: tokio_postgres::Client,
}

impl OffPeakTariffRepository {
    pub fn new(client:  tokio_postgres::Client) -> Self {
        OffPeakTariffRepository { client }
    }

    pub async fn create(&mut self, off_peak_tariff: &OffPeakTariff) -> Result<(), Error> {
        self.client
            .execute(
                "INSERT INTO off_peak_tariff (voltage_level_id, power_level_id, contract_type_id, price, year, month) VALUES ($1, $2, $3, $4, $5, $6)",
                &[&off_peak_tariff.voltage_level_id, &off_peak_tariff.power_level_id, &off_peak_tariff.contract_type_id, &off_peak_tariff.price, &(off_peak_tariff.year as i32), &(off_peak_tariff.month as i16)],
            )
            .await?;
        Ok(())
    }

    pub async fn read_all(&mut self) -> Result<Vec<OffPeakTariff>, Error> {
        let rows = self
            .client
            .query("SELECT id, voltage_level_id, power_level_id, contract_type_id, price, year, month FROM off_peak_tariff", &[])
            .await?;
        let mut results = Vec::new();
        for row in rows {
            results.push(OffPeakTariff {
                id: row.get(0),
                voltage_level_id: row.get(1),
                power_level_id: row.get(2),
                contract_type_id: row.get(3),
                price: row.get(4),
                year: row.get::<usize, i64>(5) as u32,
                month: row.get::<usize, i64>(6) as u8,
            });
        }
        Ok(results)
    }

    pub async fn update(&mut self, off_peak_tariff: &OffPeakTariff) -> Result<(), Error> {
        self.client
            .execute(
                "UPDATE off_peak_tariff SET price = $1 WHERE id = $2",
                &[
                    &off_peak_tariff.price,
                    &off_peak_tariff.id,
                ],
            )
            .await?;
        Ok(())
    }

    pub async fn delete(&mut self, id: i32) -> Result<(), Error> {
        self.client
            .execute("DELETE FROM off_peak_tariff WHERE id = $1", &[&id])
            .await?;
        Ok(())
    }

    
    pub async fn exists_by_id(&mut self, id: i32) -> Result<bool, Error> {
        let row = self.client.query_one(
            "SELECT EXISTS(SELECT 1 FROM off_peak_tariff WHERE id = $1)",
            &[&id],
        ).await?;
        let exists: bool = row.get(0);
        Ok(exists)
    }

    pub async fn exists(
        &mut self,
        voltage_level_id: i32,
        power_level_id: i32,
        contract_type_id: i32,
        year: u32,
        month: u8,
    ) -> Result<bool, Error> {
        let row = self.client.query_one(
            "SELECT EXISTS(SELECT 1 FROM off_peak_tariff WHERE voltage_level_id = $1 AND power_level_id = $2 AND contract_type_id = $3 AND year = $4 AND month = $5)",
            &[&voltage_level_id, &power_level_id, &contract_type_id, &(year as i32), &(month as i16)],
        ).await?;
        let exists: bool = row.get(0);
        Ok(exists)
    }
}
