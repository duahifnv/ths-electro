use tokio_postgres::Error;

use crate::models::Weekend;

pub struct WeekendRepository<'a> {
    client: &'a mut tokio_postgres::Client,
}

impl<'a> WeekendRepository<'a> {
    pub fn new(client: &'a mut tokio_postgres::Client) -> Self {
        WeekendRepository { client }
    }

    pub async fn create(&mut self, records: &[Weekend]) -> Result<(), Error> {
        let transaction = self.client.transaction().await?;
        for record in records {
            transaction
                .execute(
                    "INSERT INTO weekend (year, month, day) VALUES ($1, $2, $3)",
                    &[
                        &(record.year as i32),
                        &(record.month as i16),
                        &(record.day as i16),
                    ],
                )
                .await?;
        }
        transaction.commit().await?;
        Ok(())
    }

    pub async fn read_all(&mut self, year: u32, month: u8) -> Result<Vec<Weekend>, Error> {
        let year = year as i32;
        let month = month as i16;
        let rows = self
            .client
            .query(
                "SELECT id, year, month, day, hour FROM weekend WHERE year= $1 AND month = $2",
                &[&year, &month],
            )
            .await
            .expect("ababa");
        let mut results = Vec::new();
        for row in rows {
            results.push(Weekend {
                id: row.get(0),
                year: row.get::<usize, i64>(1) as u32,
                month: row.get::<usize, i64>(2) as u8,
                day: row.get::<usize, i64>(3) as u8,
            });
        }
        Ok(results)
    }

    pub async fn update(&mut self, records: &[Weekend]) -> Result<(), Error> {
        let transaction = self.client.transaction().await?;
        for record in records {
            transaction
                .execute(
                    "UPDATE weekend SET year = $1, month = $2, day = $3 WHERE id = $4",
                    &[
                        &(record.year as i32),
                        &(record.month as i16),
                        &(record.day as i16),
                        &record.id,
                    ],
                )
                .await?;
        }
        transaction.commit().await?;
        Ok(())
    }

    pub async fn delete(&mut self, year: u32, month: u8) -> Result<(), Error> {
        self.client
            .execute(
                "DELETE FROM weekend WHERE AND year = $1 AND month = $2",
                &[&(year as i32), &(month as i16)],
            )
            .await?;
        Ok(())
    }

    pub async fn exists_by_id(&mut self, year: u32, month: u8) -> Result<bool, Error> {
        let row = self
            .client
            .query_one(
                "SELECT EXISTS(SELECT 1 FROM weekend WHERE year = $1 AND month = $2)",
                &[&(year as i32), &(month as i16)],
            )
            .await?;
        let exists: bool = row.get(0);
        Ok(exists)
    }
    
    pub async fn exists(
        &mut self,
        year: u32,
        month: u8,
    ) -> Result<bool, Error> {
        let row = self.client.query_one(
            "SELECT EXISTS(SELECT 1 FROM weekend WHERE year = $1 AND month = $2)",
            &[&(year as i32), &(month as i16)],
        ).await?;
        let exists: bool = row.get(0);
        Ok(exists)
    }
}
