/**
 * 登录
 * @param {*} data 
 * @returns 
 */
export const login = (data) => {
  return usePost('/login', data)
}

/**
 * 注册
 * @param {*} data
 * @returns
 */
export const register = (data) => {
  return usePost('/register', data)
}

/**
 * 专家注册
 * @param {*} data
 * @returns
 */
export const expertRegister = (data) => {
  return usePost('/expert/register', data)
}