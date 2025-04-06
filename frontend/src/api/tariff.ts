// src/api/tariffApi.ts
import axios from 'axios';

// Конфигурация Axios
const tariffApi = axios.create({
    baseURL: 'https://your-api-base-url.com/api', // Замените на ваш базовый URL
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
});

// Интерфейсы для типизации запросов
interface AccountingHourQuery {
    voltage_level_id: number;
    price_category_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface AdditionalCostQuery {
    voltage_level_id: number;
    price_category_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface DayZoneQuery {
    month: number;
}

interface FixedTariffQuery {
    voltage_level_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface HalfPeakTariffQuery {
    voltage_level_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface HourTariffQuery {
    voltage_level_id: number;
    price_category_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface NetPowerPlanHourQuery {
    year: number;
    month: number;
}

interface NetPowerPriceQuery {
    voltage_level_id: number;
    price_category_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface OffPeakTariffQuery {
    voltage_level_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface PeakTariffQuery {
    voltage_level_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface PowerWholesalePriceQuery {
    voltage_level_id: number;
    price_category_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface PriceForOverConsumingQuery {
    voltage_level_id: number;
    price_category_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface PriceForUnderConsumingQuery {
    voltage_level_id: number;
    price_category_id: number;
    power_level_id: number;
    contract_type_id: number;
    year: number;
    month: number;
}

interface WeekendQuery {
    year: number;
    month: number;
}

// Accounting Hour API
export const accountingHourApi = {
    getAll: (params: AccountingHourQuery) => tariffApi.get('/accounting-hour', { params }),
    create: (data: AccountingHourQuery) => tariffApi.post('/accounting-hour', data),
    delete: (params: AccountingHourQuery) => tariffApi.delete('/accounting-hour', { params }),
};

// Additional Cost API
export const additionalCostApi = {
    getAll: () => tariffApi.get('/additional-cost'),
    create: (data: AdditionalCostQuery) => tariffApi.post('/additional-cost', data),
    update: (id: number, data: AdditionalCostQuery) => tariffApi.put(`/additional-cost/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/additional-cost/${id}`),
};

// Contract Type API
export const contractTypeApi = {
    getAll: () => tariffApi.get('/contract_type'),
    create: (data: any) => tariffApi.post('/contract_type', data),
    update: (id: number, data: any) => tariffApi.put(`/contract_type/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/contract_type/${id}`),
};

// Day Zone API
export const dayZoneApi = {
    getAll: (params: DayZoneQuery) => tariffApi.get('/day-zone', { params }),
    create: (data: DayZoneQuery) => tariffApi.post('/day-zone', data),
    delete: (params: DayZoneQuery) => tariffApi.delete('/day-zone', { params }),
};

// Fixed Tariff API
export const fixedTariffApi = {
    getByParams: (params: FixedTariffQuery) => tariffApi.get('/fixed-tariff', { params }),
    create: (data: FixedTariffQuery) => tariffApi.post('/fixed-tariff', data),
    update: (id: number, data: FixedTariffQuery) => tariffApi.put(`/fixed-tariff/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/fixed-tariff/${id}`),
};

// Half Peak Tariff API
export const halfPeakTariffApi = {
    getByParams: (params: HalfPeakTariffQuery) => tariffApi.get('/half-peak-tariff', { params }),
    create: (data: HalfPeakTariffQuery) => tariffApi.post('/half-peak-tariff', data),
    update: (id: number, data: HalfPeakTariffQuery) => tariffApi.put(`/half-peak-tariff/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/half-peak-tariff/${id}`),
};

// Hour Tariff API
export const hourTariffApi = {
    getAll: (params: HourTariffQuery) => tariffApi.get('/hour-tariff', { params }),
    create: (data: HourTariffQuery) => tariffApi.post('/hour-tariff', data),
    delete: (params: HourTariffQuery) => tariffApi.delete('/hour-tariff', { params }),
};

// Net Power Plan Hours API
export const netPowerPlanHoursApi = {
    getAll: (params: NetPowerPlanHourQuery) => tariffApi.get('/net-power-plan-hours', { params }),
    create: (data: NetPowerPlanHourQuery) => tariffApi.post('/net-power-plan-hours', data),
    delete: (params: NetPowerPlanHourQuery) => tariffApi.delete('/net-power-plan-hours', { params }),
};

// Net Power Price API
export const netPowerPriceApi = {
    getByParams: (params: NetPowerPriceQuery) => tariffApi.get('/net-power-price', { params }),
    create: (data: NetPowerPriceQuery) => tariffApi.post('/net-power-price', data),
    update: (id: number, data: NetPowerPriceQuery) => tariffApi.put(`/net-power-price/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/net-power-price/${id}`),
};

// Off Peak Tariff API
export const offPeakTariffApi = {
    getByParams: (params: OffPeakTariffQuery) => tariffApi.get('/off-peak-tariff', { params }),
    create: (data: OffPeakTariffQuery) => tariffApi.post('/off-peak-tariff', data),
    update: (id: number, data: OffPeakTariffQuery) => tariffApi.put(`/off-peak-tariff/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/off-peak-tariff/${id}`),
};

// Peak Tariff API
export const peakTariffApi = {
    getByParams: (params: PeakTariffQuery) => tariffApi.get('/peak-tariff', { params }),
    create: (data: PeakTariffQuery) => tariffApi.post('/peak-tariff', data),
    update: (id: number, data: PeakTariffQuery) => tariffApi.put(`/peak-tariff/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/peak-tariff/${id}`),
};

// Power Level API
export const powerLevelApi = {
    getAll: () => tariffApi.get('/power-level'),
    create: (data: any) => tariffApi.post('/power-level', data),
    update: (id: number, data: any) => tariffApi.put(`/power-level/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/power-level/${id}`),
};

// Power Wholesale Price API
export const powerWholesalePriceApi = {
    getByParams: (params: PowerWholesalePriceQuery) => tariffApi.get('/power-wholesale-price', { params }),
    create: (data: PowerWholesalePriceQuery) => tariffApi.post('/power-wholesale-price', data),
    update: (id: number, data: PowerWholesalePriceQuery) => tariffApi.put(`/power-wholesale-price/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/power-wholesale-price/${id}`),
};

// Price Category API
export const priceCategoryApi = {
    getAll: () => tariffApi.get('/price-category'),
    create: (data: any) => tariffApi.post('/price-category', data),
    update: (id: number, data: any) => tariffApi.put(`/price-category/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/price-category/${id}`),
};

// Price For Over Consuming API
export const priceForOverConsumingApi = {
    getByParams: (params: PriceForOverConsumingQuery) => tariffApi.get('/price-for-over-consuming', { params }),
    create: (data: PriceForOverConsumingQuery) => tariffApi.post('/price-for-over-consuming', data),
    update: (id: number, data: PriceForOverConsumingQuery) => tariffApi.put(`/price-for-over-consuming/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/price-for-over-consuming/${id}`),
};

// Price For Under Consuming API
export const priceForUnderConsumingApi = {
    getByParams: (params: PriceForUnderConsumingQuery) => tariffApi.get('/price-for-under-consuming', { params }),
    create: (data: PriceForUnderConsumingQuery) => tariffApi.post('/price-for-under-consuming', data),
    update: (id: number, data: PriceForUnderConsumingQuery) => tariffApi.put(`/price-for-under-consuming/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/price-for-under-consuming/${id}`),
};

// Voltage Level API
export const voltageLevelApi = {
    getAll: () => tariffApi.get('/voltage-level'),
    create: (data: any) => tariffApi.post('/voltage-level', data),
    update: (id: number, data: any) => tariffApi.put(`/voltage-level/${id}`, data),
    delete: (id: number) => tariffApi.delete(`/voltage-level/${id}`),
};

// Weekend API
export const weekendApi = {
    getAll: (params: WeekendQuery) => tariffApi.get('/weekend', { params }),
    create: (data: WeekendQuery) => tariffApi.post('/weekend', data),
    delete: (params: WeekendQuery) => tariffApi.delete('/weekend', { params }),
};

export default tariffApi;