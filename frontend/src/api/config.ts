import axios from "axios";

export const API_BASE_URL = "http://api.envelope42.ru/api";

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(
    (config) => {
        const auth = localStorage.getItem('auth'); // Получаем токен из localStorage
        if (auth != null) {
            const token = JSON.parse(auth).token;
            config.headers.Authorization = `Bearer ${token}`; // Добавляем токен в заголовок
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response.status === 401) {
            localStorage.removeItem('auth');
            // window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;