import { Auth } from '../interfaces/Auth';
import { User } from '../interfaces/User';
import api from './config';

export const login = async (login: string, password: string) => {
    const response = await api.post(`/identity/account/login`, {
        username: login,
        password: password
    });
    return response;
};

export const register = async (auth: { login: string, password: string }, user: User) => {
    const response = await api.post(`/identity/account/register`, {
        username: auth.login,
        email: user.email, 
        password: auth.password,
        firstname: user.firstname, 
        lastname: user.lastname, 
        middlename: user.middlename, 
    });
    return response;
};
