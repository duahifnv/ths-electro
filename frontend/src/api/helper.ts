import api from "./config";

export const getHelper = async (id: string) => {
    const response = await api.get(`/helper/admin/${id}`);
    return response;
};

export const updateHelper = async (id: string, data: { firstname: string; lastname: string }) => {
    const response = await api.put(`/helper/admin/${id}`, data);
    return response;
};

export const deleteHelper = async (id: string) => {
    const response = await api.delete(`/helper/admin/${id}`);
    return response;
};

export const getAllHelpers = async () => {
    const response = await api.get(`/helper/admin`);
    return response;
};

export const createHelper = async (data: { tgId: string; firstname: string; lastname: string }) => {
    const response = await api.post(`/helper/admin`, data);
    return response;
};