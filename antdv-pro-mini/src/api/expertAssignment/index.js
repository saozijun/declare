/**
 * 分配专家
 * @param {*} applicationId 
 * @param {*} expertIds 
 * @returns 
 */
export const assignExperts = (applicationId, expertIds) => {
  return usePost('/project/application/assign-experts?applicationId=' + applicationId, expertIds);
};

/**
 * 获取可分配的专家列表
 * @returns 
 */
export const getExperts = () => {
  return useGet('/project/application/experts');
};

/**
 * 获取项目已分配的专家
 * @param {*} applicationId 
 * @returns 
 */
export const getAssignedExperts = (applicationId) => {
  return useGet('/expert/assignment/assigned-experts?applicationId=' + applicationId);
};

/**
 * 获取项目已分配的专家详细信息
 * @param {*} applicationId 
 * @returns 
 */
export const getAssignedExpertsDetail = (applicationId) => {
  return useGet('/expert/assignment/assigned-experts-detail?applicationId=' + applicationId);
};

/**
 * 获取专家分配列表
 * @param {*} data 
 * @returns 
 */
export const getAssignmentList = (data) => {
  return useGet('/expert/assignment/list', data);
}; 