import api, { API_BASE_URL } from "./config";

export const url = (filename: string, section: string = 'avatars') => {
    return `${API_BASE_URL}/images/${section}/${filename}`;
};

export const getavatar = async (name: string) => {
    const response = await api.get(`images/avatars/${name}`);
    return response;
};