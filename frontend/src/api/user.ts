import { Auth } from '../interfaces/Auth';
import { User } from '../interfaces/User';
import api from './config';

export const get = async (data: { tag?: string, username?: string, id?: string, email?: string }) => {
    var response;

    if (data.tag) {
        response = api.get(`identity/users/tag/${data.tag}`);
    } else if (data.username) {
        response = api.get(`identity/users/username/${data.username}`);
    } else if (data.id) {
        response = api.get(`identity/users/id/${data.id}`);
    } else if (data.email) {
        response = api.get(`identity/users/email/${data.email}`);
    } else {
        return null;
    }

    return response;
};

export const me = async () => {
    const response = await api.get("/identity/users/me");
    return response;
};

export const roles = async () => {
    const response = await api.get('/identity/users/me/roles');
    return response;
};

export const update = async (user: User) => {
    const response = await api.put(`/identity/users/me`, {
        email: user.email,
        tag: user.tag,
        firstname: user.firstname,
        lastname: user.lastname,
        middlename: user.middlename
    });
    return response;
};

export const updatepassword = async (password: string) => {
    const response = await api.put(`/identity/users/me/password`, {
        password: password
    });
    return response;
};

export const updateavatar = async (file: any) => {
    const formData = new FormData();
    formData.append('imageFile', file);

    const response = await api.put("/identity/users/me/avatar", formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
    return response;
};

export const deleteavatar = async () => {
    const response = await api.delete("/identity/users/me/avatar");
    return response;
};