/**
 * 列表
 * @param {*} data 
 * @returns 
 */
export const getApplicationPage = (data) => {
  return useGet('/project/application/page', data);
};

/**
 * 提交
 * @param {*} data 
 * @returns 
 */
export const submitApplication = (data) => {
  return usePost('/project/application/submit', data);
};


export const approveApplication = (id) => {
  return usePost(`/project/application/review?id=${id}&status=1`);
};

export const rejectApplication = (id) => {
  return usePost(`/project/application/review?id=${id}&status=2`);
};


/**
 * 新增编辑
 * @param {*} data 
 * @returns 
 */
export const save = (data) => {
  return usePost('/project/application/save', data);
};

/**
 * 删除
 * @param {*} data 
 * @returns 
 */
export const del = (data) => {
  return usePost('/project/application/delete', data);
};

/**
 * 获取可分配的专家列表
 * @returns 
 */
export const getExperts = () => {
  return useGet('/project/application/experts');
};

/**
 * 分配专家
 * @param {*} applicationId 
 * @param {*} expertIds 
 * @returns 
 */
export const assignExperts = (applicationId, expertIds) => {
  return usePost('/project/application/assign-experts?applicationId=' + applicationId, expertIds);
};
