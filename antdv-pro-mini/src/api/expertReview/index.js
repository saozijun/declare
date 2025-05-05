/**
 * 提交评审
 * @param {*} data 
 * @returns 
 */
export const submitReview = (data) => {
  return usePost('/expert/review/submit', data);
};

/**
 * 保存评审草稿
 * @param {*} data 
 * @returns 
 */
export const saveReviewDraft = (data) => {
  return usePost('/expert/review/save-draft', data);
};

/**
 * 获取评审列表
 * @param {*} data 
 * @returns 
 */
export const getReviewList = (data) => {
  return useGet('/expert/review/list', data);
};

/**
 * 获取评审详情
 * @param {*} id 
 * @returns 
 */
export const getReviewDetail = (id) => {
  return useGet('/expert/review/detail?id=' + id);
};

/**
 * 根据分配ID获取评审
 * @param {*} assignmentId 
 * @returns 
 */
export const getReviewByAssignment = (assignmentId) => {
  return useGet('/expert/review/by-assignment?assignmentId=' + assignmentId);
}; 