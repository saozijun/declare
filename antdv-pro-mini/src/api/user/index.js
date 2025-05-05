/**
 * 列表
 * @param {*} data 
 * @returns 
 */
export const list = (data) => {
  return useGet('/user/page', data);
};

/**
 * 获取专家列表
 * @returns 
 */
export const getExpertList = () => {
  return useGet('/user/expert/list');
};

/**
 * 新增编辑
 * @param {*} data 
 * @returns 
 */
export const save = (data) => {
  return usePost('/user/save', data);
};

/**
 * 删除
 * @param {*} data 
 * @returns 
 */
export const del = (data) => {
  return usePost('/user/delete', data);
};

/**
 * 更新用户状态
 * @param {*} data 
 * @returns 
 */
export const updateStatus = (data) => {
  return usePost('/user/updateStatus', data);
};

/**
 * 更新信息
 * @param {*} data 
 * @returns 
 */
export const updateInfo = (data) => {
  return usePost('/user/upload/avatar', data);
};

/**
 * 更新信息
 * @param {*} data 
 * @returns 
 */
export const getInfo = (id) => {
  return useGet('/user/getById?id=' + id);
};