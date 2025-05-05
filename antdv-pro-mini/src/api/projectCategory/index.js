/**
 * 获取分类分页列表
 * @param {*} data 
 * @returns 
 */
export const getCategoryPage = (data) => {
  return useGet('/project/category/page', data);
};

/**
 * 获取所有分类列表
 * @returns 
 */
export const getCategoryList = () => {
  return useGet('/project/category/list');
};

/**
 * 获取分类详情
 * @param {*} data 
 * @returns 
 */
export const getCategoryById = (data) => {
  return useGet('/project/category/getById', data);
};

/**
 * 保存或更新分类
 * @param {*} data 
 * @returns 
 */
export const saveCategory = (data) => {
  return usePost('/project/category/save', data);
};

/**
 * 删除分类
 * @param {*} data 
 * @returns 
 */
export const deleteCategory = (data) => {
  return usePost('/project/category/delete', data);
};

/**
 * 批量删除分类
 * @param {*} data 
 * @returns 
 */
export const batchDeleteCategory = (data) => {
  return usePost('/project/category/del/batch', data);
};