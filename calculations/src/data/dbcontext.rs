use bb8::{Pool, PooledConnection};
use bb8_postgres::PostgresConnectionManager;
use tokio_postgres::NoTls;

pub type ConnectionPool = Pool<PostgresConnectionManager<NoTls>>;
pub type Connection = PooledConnection<'static, PostgresConnectionManager<NoTls>>;

pub async fn create_pool() -> Result<ConnectionPool, Box<dyn std::error::Error>> {
    let database_url =
        "host=db user=postgres password=postgres dbname=electricity_hack_calculation_db";

    let manager = PostgresConnectionManager::new_from_stringlike(database_url, NoTls)?;
    let pool = Pool::builder().build(manager).await?;

    Ok(pool)
}

pub async fn create_tables(pool: &ConnectionPool) -> Result<(), Box<dyn std::error::Error>> {
    let conn = pool.get().await?;

    let queries = vec![
        r#"
        CREATE TABLE IF NOT EXISTS voltage_level (
            id SERIAL PRIMARY KEY,
            name TEXT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS power_level (
            id SERIAL PRIMARY KEY,
            name TEXT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS contract_type (
            id SERIAL PRIMARY KEY,
            name TEXT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS fixed_tariff (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS day_zone (
            id SERIAL PRIMARY KEY,
            zone_type SMALLINT NOT NULL,
            month SMALLINT NOT NULL,
            hour SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS off_peak_tariff (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS half_peak_tariff (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS peak_tariff (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS price_category (
            id SERIAL PRIMARY KEY,
            name TEXT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS hour_tariff (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            price_category_id BIGINT REFERENCES price_category(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL,
            day SMALLINT NOT NULL,
            hour SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS accounting_hour (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            price_category_id BIGINT REFERENCES price_category(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL,
            day SMALLINT NOT NULL,
            hour SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS power_wholesale_price (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            price_category_id BIGINT REFERENCES price_category(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS net_power_plan_hours (
            id SERIAL PRIMARY KEY,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL,
            day SMALLINT NOT NULL,
            hour SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS net_power_price (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            price_category_id BIGINT REFERENCES price_category(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            price DOUBLE PRECISION NOT NULL,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS price_for_under_consuming (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            price_category_id BIGINT REFERENCES price_category(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL,
            price DOUBLE PRECISION NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS price_for_over_consuming (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            price_category_id BIGINT REFERENCES price_category(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL,
            price DOUBLE PRECISION NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS additional_cost (
            id SERIAL PRIMARY KEY,
            voltage_level_id BIGINT REFERENCES voltage_level(id),
            price_category_id BIGINT REFERENCES price_category(id),
            power_level_id BIGINT REFERENCES power_level(id),
            contract_type_id BIGINT REFERENCES contract_type(id),
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL,
            price DOUBLE PRECISION NOT NULL,
            name TEXT NOT NULL
        );
        "#,
        r#"
        CREATE TABLE IF NOT EXISTS weekend (
            id SERIAL PRIMARY KEY,
            year INTEGER NOT NULL,
            month SMALLINT NOT NULL,
            day SMALLINT NOT NULL
        );
        "#,
    ];

    for query in queries {
        conn.execute(query, &[]).await.unwrap();
    }

    let row = conn
        .query_one("SELECT COUNT(*) FROM contract_type", &[])
        .await
        .unwrap();
    let number: i64 = row.get(0);
    if number == 0 {
        let names = ["Купля-продажа электроэнергии", "Договор электроснабжения"];
        for name in names {
            conn.execute("INSERT INTO contract_type (name) VALUES ($1)", &[&name])
                .await?;
        }
    }

    let row = conn
        .query_one("SELECT COUNT(*) FROM power_level", &[])
        .await
        .unwrap();
    let number: i64 = row.get(0);
    if number == 0 {
        let names = ["Менее 679 КВт", "670 кВт - 10 МВт", "Более 10 МВт"];
        for name in names {
            conn.execute("INSERT INTO power_level (name) VALUES ($1)", &[&name])
                .await?;
        }
    }

    let row = conn
        .query_one("SELECT COUNT(*) FROM price_category", &[])
        .await
        .unwrap();
    let number: i64 = row.get(0);
    if number == 0 {
        let names = ["ЦК1", "ЦК2", "ЦК3", "ЦК4", "ЦК5", "ЦК6"];
        for name in names {
            conn.execute("INSERT INTO price_category (name) VALUES ($1)", &[&name])
                .await?;
        }
    }

    let row = conn
        .query_one("SELECT COUNT(*) FROM voltage_level", &[])
        .await
        .unwrap();
    let number: i64 = row.get(0);
    if number == 0 {
        let names = ["ВН", "СН-1", "СН-2", "НН"];
        for name in names {
            conn.execute("INSERT INTO voltage_level (name) VALUES ($1)", &[&name])
                .await?;
        }
    }

    Ok(())
}
