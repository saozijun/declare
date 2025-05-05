
export const list = () => {
  return useGet('/expert/list');
};


export const save = (data) => {
  return usePost('/expert/save', data);
};

export const del = (data) => {
  return usePost('/expert/delete', data);
};


export const updateStatus = (id, status) => {
  return usePost(`/expert/status/${id}?status=${status}`);
};

export const update = (data) => {
  return usePost('/expert/update', data);
};


export const getByUserId = (userId) => {
  return useGet(`/expert/${userId}`);
};

export const getAvailableUsers = (userId) => {
  return useGet(`/expert/available-users`);
};