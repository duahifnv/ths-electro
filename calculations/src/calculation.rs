pub fn calculate_cost_category_1(fixed_tariff: f64, total_consumtion: f64) -> f64 {
    fixed_tariff * total_consumtion
}

pub fn calculate_cost_category_2(
    off_peak_tariff: f64,
    half_peak_tariff: f64,
    peak_tariff: f64,
    hours_category: Vec<u8>,
    consumption_per_hour: Vec<Vec<f64>>,
) -> f64 {
    let mut total_cost: f64 = 0.;
    let tariffs = [off_peak_tariff, half_peak_tariff, peak_tariff];
    for day in consumption_per_hour {
        for i in 0..24 {
            total_cost += day[i] * tariffs[hours_category[i] as usize];
        }
    }

    total_cost
}

pub fn calculate_cost_category_3(
    tariff_per_hour: Vec<Vec<f64>>,
    consumption_per_hour: Vec<Vec<f64>>,
    accounting_hours: Vec<u8>,
    power_wholesale_price: f64,
    number_of_working_days: u8,
) -> f64 {
    let electricity_cost: f64 = consumption_per_hour
        .iter()
        .zip(tariff_per_hour)
        .map(|x| {
            let a: f64 = (*x.1).iter().zip(x.0).map(|y| *y.0 * *y.1).sum();
            return a;
        })
        .sum();

    let accounting_power: f64 = consumption_per_hour
        .iter()
        .zip(accounting_hours)
        .map(|x| x.0[x.1 as usize])
        .sum();

    let power_wholesale_cost =
        accounting_power / number_of_working_days as f64 * power_wholesale_price;

    electricity_cost + power_wholesale_cost
}

pub fn calculate_cost_category_4(
    tariff_per_hour: Vec<Vec<f64>>,
    consumption_per_hour: Vec<Vec<f64>>,
    accounting_hours: Vec<u8>,
    power_wholesale_price: f64,
    number_of_working_days: u8,
    net_power_plan_hours: Vec<Vec<u8>>,
    net_power_price: f64,
) -> f64 {
    let electricity_cost: f64 = consumption_per_hour
        .iter()
        .zip(tariff_per_hour)
        .map(|x| {
            let a: f64 = (*x.1).iter().zip(x.0).map(|y| *y.0 * *y.1).sum();
            return a;
        })
        .sum();

    let accounting_power: f64 = consumption_per_hour
        .iter()
        .zip(accounting_hours)
        .map(|x| x.0[x.1 as usize])
        .sum();

    let power_wholesale_cost =
        accounting_power / number_of_working_days as f64 * power_wholesale_price;

    let net_power_max_consuming: f64 = consumption_per_hour
        .iter()
        .zip(net_power_plan_hours)
        .map(|(day, plan_day)| {
            plan_day
                .iter()
                .map(|x| day[*x as usize])
                .max_by(|a, b| a.total_cmp(b))
                .unwrap()
        })
        .sum();

    let net_power_cost = net_power_max_consuming / number_of_working_days as f64 * net_power_price;

    electricity_cost + power_wholesale_cost + net_power_cost
}

pub fn calculate_cost_category_5(
    tariff_per_hour: Vec<Vec<f64>>,
    consumption_per_hour: Vec<Vec<f64>>,
    planed_consumption_per_hour: Vec<Vec<f64>>,
    price_for_under_consuming: f64,
    price_for_over_consuming: f64,
    accounting_hours: Vec<u8>,
    power_wholesale_price: f64,
    number_of_working_days: u8,
) -> f64 {
    let electricity_cost: f64 = consumption_per_hour
        .iter()
        .zip(tariff_per_hour)
        .map(|x| {
            let a: f64 = (*x.1).iter().zip(x.0).map(|y| *y.0 * *y.1).sum();
            return a;
        })
        .sum();

    let unplaned_electricity_cost: f64 = consumption_per_hour
        .iter()
        .zip(planed_consumption_per_hour)
        .map(|x| {
            let a: f64 = (*x.1)
                .iter()
                .zip(x.0)
                .map(|(consumption, planed_consumtion)| {
                    let delta = consumption - planed_consumtion;
                    if delta > 0. {
                        delta * price_for_over_consuming
                    } else if delta < 0. {
                        delta * price_for_under_consuming
                    } else {
                        0.
                    }
                })
                .sum();
            return a;
        })
        .sum();

    let accounting_power: f64 = consumption_per_hour
        .iter()
        .zip(accounting_hours)
        .map(|x| x.0[x.1 as usize])
        .sum();

    let power_wholesale_cost =
        accounting_power / number_of_working_days as f64 * power_wholesale_price;

    electricity_cost + unplaned_electricity_cost + power_wholesale_cost
}

pub fn calculate_cost_category_6(
    tariff_per_hour: Vec<Vec<f64>>,
    consumption_per_hour: Vec<Vec<f64>>,
    planed_consumption_per_hour: Vec<Vec<f64>>,
    price_for_under_consuming: f64,
    price_for_over_consuming: f64,
    accounting_hours: Vec<u8>,
    power_wholesale_price: f64,
    number_of_working_days: u8,
    net_power_plan_hours: Vec<Vec<u8>>,
    net_power_price: f64,
) -> f64 {
    let electricity_cost: f64 = consumption_per_hour
        .iter()
        .zip(tariff_per_hour)
        .map(|x| {
            let a: f64 = (*x.1).iter().zip(x.0).map(|y| *y.0 * *y.1).sum();
            return a;
        })
        .sum();

    let unplaned_electricity_cost: f64 = consumption_per_hour
        .iter()
        .zip(planed_consumption_per_hour)
        .map(|x| {
            let a: f64 = (*x.1)
                .iter()
                .zip(x.0)
                .map(|(consumption, planed_consumtion)| {
                    let delta = consumption - planed_consumtion;
                    if delta > 0. {
                        delta * price_for_over_consuming
                    } else if delta < 0. {
                        delta * price_for_under_consuming
                    } else {
                        0.
                    }
                })
                .sum();
            return a;
        })
        .sum();

    let accounting_power: f64 = consumption_per_hour
        .iter()
        .zip(accounting_hours)
        .map(|x| x.0[x.1 as usize])
        .sum();

    let power_wholesale_cost =
        accounting_power / number_of_working_days as f64 * power_wholesale_price;

    let net_power_max_consuming: f64 = consumption_per_hour
        .iter()
        .zip(net_power_plan_hours)
        .map(|(day, plan_day)| {
            plan_day
                .iter()
                .map(|x| day[*x as usize])
                .max_by(|a, b| a.total_cmp(b))
                .unwrap()
        })
        .sum();

    let net_power_cost = net_power_max_consuming / number_of_working_days as f64 * net_power_price;

    electricity_cost + unplaned_electricity_cost + power_wholesale_cost + net_power_cost
}
