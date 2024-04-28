import axios from '../utils/axios'
import env from '../config/env'



/**
 * 分页搜索待审核接口
 * @param params
 * @returns
 */
export function search(params) {
    return axios.get(env.host.base + '/m2c/pic/search', { params: params });
}
  
  /**
   * 根据ID查询详情数据
   * @param params
   * @returns
   */
export function searchById(params) {
    return axios.get(env.host.base + '/m2c/pic/searchById', { params: params });
  }
  
  /**
   * 修改textAnnotations文本
   * @param params
   * @returns
   */
export function updateTextAnno(data) {
    const token = localStorage.getItem('token');
    if(!token){
        return Promise.reject()
    }
    return axios.post(env.host.base + '/m2c/pic/updateTextAnno', data, {
        headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}`
        }
    });
  }
  
  /**
   * 审核通过接口
   * @param params
   * @returns
   */
export function audit(data) {
    return axios.post(env.host.base + '/m2c/pic/audit?id='+ data);
  }
  