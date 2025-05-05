/**
 * 获取项目分页列表
 * @param {*} data 
 * @returns 
 */
export const getProjectPage = (data) => {
  return useGet('/project/page', data);
};

/**
 * 获取项目详情
 * @param {*} data 
 * @returns 
 */
export const getProjectById = (data) => {
  return useGet('/project/getById', data);
};

/**
 * 保存或更新项目
 * @param {*} data 
 * @returns 
 */
export const saveProject = (data) => {
  return usePost('/project/save', data);
};

/**
 * 删除项目
 * @param {*} data 
 * @returns 
 */
export const deleteProject = (data) => {
  return usePost('/project/delete', data);
};

/**
 * 批量删除项目
 * @param {*} data 
 * @returns 
 */
export const batchDeleteProject = (data) => {
  return usePost('/project/del/batch', data);
};


/**
 * 发布
 * @param {*} id 
 * @returns 
 */
export const publishProject = (id) => {
  return usePost('/project/publish?id=' + id);
};


/**
 * 取消发布
 * @param {*} id 
 * @returns 
 */
export const unpublishProject = (id) => {
  return usePost('/project/unpublish?id=' + id);
};


