import axios from '../utils/axios'
import env from '../config/env'
// ==============邮件===============================================

/**
 * 模板列表
 * @param {*} params 
 * @returns 
 */
export function templatePage(params) {
    return axios.get(env.host.base + '/zx/template/page', { params: params });
}

/**
 * 添加模板
 * @param {*} params 
 * @returns 
 */
export function templateSave(params) {
    return axios.post(env.host.base + '/zx/template/save', params);
}

/**
 * 根据id修改模板
 * @param {*} params 
 * @returns 
 */
export function templateUpdate(params) {
    return axios.post(env.host.base + '/zx/template/update', params);
}

/**
 * 删除模板
 * @param {*} params 
 * @returns 
 */
export function templateDel(params) {
    return axios.get(env.host.base + '/zx/template/delete', { params: params });
}


/**
 * 日志分页
 * @param {*} params 
 * @returns 
 */
export function logPage(params) {
    return axios.get(env.host.base + '/zx/template/logPage', { params: params });
}