import axios from '../utils/axios'
import env from '../config/env'
// ==================登录=============================
/**
 * 登录
 * @param params
 * @returns {Promise<AxiosResponse<T>>}
 */
export function login(params) {
    return axios.post(env.host.base + '/zx/user/login', params);
}

/**
 * 微信扫码登录获取二维码
 * @param params
 * @returns {Promise<AxiosResponse<T>>}
 */
export function wxLogin(params) {
    return axios.post(env.host.base + '/zx/auth/wxLogin', params);
}

/**
 * 微信扫码登录获取token
 * @param params
 * @returns {Promise<AxiosResponse<T>>}
 */
export function wxCallback(params) {
    return axios.post(env.host.base + '/zx/auth/wxCallback', params);
}

/**
 * 登出
 * @param params
 * @returns {Promise<AxiosResponse<T>>}
 */
export function logout(params) {
    return axios.post(env.host.base + '/zx/user/logout', params);
}


// =================数据字典============================
/**
 * 分页列表
 * @param {*} params 
 * @returns 
 */
export function dicList(params) {
    return axios.get(env.host.base + '/zx/dic/dicList', { params: params });
}

/**
 * 列表
 * @param {*} params 
 * @returns 
 */
export function getGroupNameByList(params) {
    return axios.get(env.host.base + '/zx/dic/getGroupNameByList', { params: params });
}

/**
 * 添加字典
 * @param {*} params 
 * @returns 
 */
export function dicSave(params) {
    return axios.post(env.host.base + '/zx/dic/save', params);
}

/**
 * 修改字典
 * @param {*} params 
 * @returns 
 */
export function dicUpdate(params) {
    return axios.post(env.host.base + '/zx/dic/update', params);
}

/**
 * 删除字典
 * @param {*} params 
 * @returns 
 */
export function dicDel(params) {
    return axios.get(env.host.base + '/zx/dic/del', { params: params });
}

// =================用户信息===========================
/**
 * 用户列表分页
 * @param {*} params 
 * @returns 
 */
export function userPage(params) {
    return axios.get(env.host.base + '/zx/user/page', { params: params });
}

/**
 * 用户列表
 * @param {*} params 
 * @returns 
 */
export function userList(params) {
    return axios.get(env.host.base + '/zx/user/list', { params: params });
}

/**
 * 添加用户
 * @param {*} params 
 * @returns 
 */
export function addUser(params) {
    return axios.post(env.host.base + '/zx/user/save', params);
}

/**
 * 修改用户
 * @param {*} params 
 * @returns 
 */
export function updateUser(params) {
    return axios.post(env.host.base + '/zx/user/update', params);
}

/**
 * 删除用户
 * @param {*} params 
 * @returns 
 */
export function dicUser(params) {
    return axios.get(env.host.base + '/zx/user/del', { params: params });
}

/**
 * 修改密码
 * @param {*} params 
 * @returns 
 */
export function modifyPass(params) {
    return axios.post(env.host.base + '/zx/user/modifyPass', params);
}

// =====================菜单管理==================================

/**
 * 列表
 * @param {*} params 
 * @returns 
 */
export function menuList(params) {
    return axios.get(`${env.host.base}/zx/menu/list`, { params: params });
}

/**
 * 删除菜单
 * @param {*} params 
 * @returns 
 */
export function delMenu(params) {
    return axios.get(env.host.base + '/zx/menu/del', { params: params });
}

/**
 * 添加菜单
 * @param {*} params 
 * @returns 
 */
export function saveMenu(params) {
    return axios.post(env.host.base + '/zx/menu/save', params);
}

/**
 * 更新菜单
 * @param {*} params 
 * @returns 
 */
export function updateMenu(params) {
    return axios.post(env.host.base + '/zx/menu/update', params);
}

// =================角色管理============================

/**
 * 分页列表
 * @param {*} params 
 * @returns 
 */
export function rolePage(params) {
    return axios.get(env.host.base + '/zx/role/page', { params: params });
}

/**
 * 列表
 * @param {*} params 
 * @returns 
 */
export function roleList(params) {
    return axios.get(env.host.base + '/zx/role/list', { params: params });
}

/**
 * 删除角色
 * @param {*} params 
 * @returns 
 */
export function delRole(params) {
    return axios.get(env.host.base + '/zx/role/del', { params: params });
}

/**
 * 添加角色
 * @param {*} params 
 * @returns 
 */
export function saveRole(params) {
    return axios.post(env.host.base + '/zx/role/save', params);
}

/**
 * 更新角色
 * @param {*} params 
 * @returns 
 */
export function updateRole(params) {
    return axios.post(env.host.base + '/zx/role/update', params);
}

/**
 * 菜单角色列表
 * @param {*} params 
 * @returns 
 */
export function menuRoleList(params) {
    return axios.get(env.host.base + '/zx/role/menuRoleList', { params: params });
}

/**
 * 分配菜单角色
 * @param {*} params 
 * @returns 
 */
export function menuRoleAdd(params) {
    return axios.post(env.host.base + '/zx/role/menuRoleAdd', params);
}

/**
 * 用户角色详情
 * @param {*} params 
 * @returns 
 */
export function userRoleInfo(params) {
    return axios.get(env.host.base + '/zx/role/userRoleInfo', { params: params });
}

/**
 * 分配用户角色
 * @param {*} params 
 * @returns 
 */
export function userRoleAdd(params) {
    return axios.post(env.host.base + '/zx/role/userRoleAdd', params);
}

// ====================组织机构=========================
/**
 * 组织分页列表
 * @param {*} params 
 * @returns 
 */
export function orgList(params) {
    return axios.get(env.host.base + '/zx/org/list', { params: params });
}

/**
 * 删除组织
 * @param {*} params 
 * @returns 
 */
export function delOrg(params) {
    return axios.get(env.host.base + '/zx/org/del', { params: params });
}

/**
 * 添加组织
 * @param {*} params 
 * @returns 
 */
export function saveOrg(params) {
    return axios.post(env.host.base + '/zx/org/save', params);
}

/**
 * 更新组织
 * @param {*} params 
 * @returns 
 */
export function updateOrg(params) {
    return axios.post(env.host.base + '/zx/org/update', params);
}

/**
 * 组织角色详情
 * @param {*} params 
 * @returns 
 */
export function orgRoleInfo(params) {
    return axios.get(env.host.base + '/zx/role/orgRoleInfo', { params: params });
}

/**
 * 分配组织角色
 * @param {*} params 
 * @returns 
 */
export function orgRoleAdd(params) {
    return axios.post(env.host.base + '/zx/role/orgRoleAdd', params);
}

// ==============日志===============================================

/**
 * 操作日志分页
 * @param {*} params 
 * @returns 
 */
export function logInfoPage(params) {
    return axios.get(env.host.base + '/zx/log/logInfoPage', { params: params });
}

/**
 * 异常日志分页
 * @param {*} params 
 * @returns 
 */
export function logErrorPage(params) {
    return axios.get(env.host.base + '/zx/log/logErrorPage', { params: params });
}

// ====================任务管理=========================
/**
 * 任务分页
 * @param {*} params 
 * @returns 
 */
export function taskPage(params) {
    return axios.get(env.host.base + '/zx/taskConfig/page', { params: params });
}

/**
 * 删除任务
 * @param {*} params 
 * @returns 
 */
export function delTask(params) {
    return axios.get(env.host.base + '/zx/taskConfig/del', { params: params });
}

/**
 * 添加任务
 * @param {*} params 
 * @returns 
 */
export function saveTask(params) {
    return axios.post(env.host.base + '/zx/taskConfig/save', params);
}

/**
 * 修改任务
 * @param {*} params 
 * @returns 
 */
export function updateTask(params) {
    return axios.post(env.host.base + '/zx/taskConfig/update', params);
}

/**
 * 任务执行日志分页
 * @param {*} params 
 * @returns 
 */
export function taskLogPage(params) {
    return axios.get(env.host.base + '/zx/taskLog/page', { params: params });
}

/**
 * 清空日志
 * @param {*} params 
 * @returns 
 */
export function taskLogClear(params) {
    return axios.get(env.host.base + '/zx/taskLog/clear', { params: params });
}

// ====================采集管理=========================
/**
 * 采集任务分页
 * @param {*} params 
 * @returns 
 */
export function collectPage(params) {
    return axios.get(env.host.base + '/zx/collect/page', { params: params });
}
/**
 * 采集数据分页
 * @param {*} params 
 * @returns 
 */
export function collectDataPage(params) {
    return axios.get(env.host.base + '/zx/collect/dataPage', { params: params });
}
/**
 * 导出采集数据分页
 * @param {*} params 
 * @returns 
 */
export function exportTxt(params) {
    return axios({
        method: 'get',
        url: env.host.base + '/zx/collect/exportTxt',
        responseType: 'blob',
        params: params
      });

}

export function exportSrt(params) {
    return axios({
        method: 'get',
        url: env.host.base + '/zx/collect/exportSrt',
        responseType: 'blob',
        params: params
      });

}
export function exportWAV(params) {
    return axios({
        method: 'get',
        url: env.host.base + '/zx/collect/exportWAV',
        responseType: 'blob',
        params: params
      });

}
/**
 * 采集管理
 * @param {*} params 
 * @returns 
 */
export function collect(params) {
    return axios.get(env.host.base + '/zx/collect/record', { params: params });
}
/**
 * 采集管理
 * @param {*} params 
 * @returns 
 */
export function sendNote(params) {
    return axios.get(env.host.base + '/zx/collect/sendNote', { params: params });

}

export function updateNote(params) {
    return axios.get(env.host.base + '/zx/collect/updateNote', { params: params });
}



/**
 * 创建或更新
 * @param {*} params 
 * @returns 
 */
export function createNote(params) {
    return axios.get(env.host.base + '/zx/collect/createNote', { params: params });
  
}

/**
 * 切割
 * @param {*} params 
 * @returns 
 */
export function cuttingRecord(params) {
    return axios.post(env.host.base + '/zx/collect/cuttingRecord', params);
}
/**
 * 合并
 * @param {*} params 
 * @returns 
 */
export function mergeRecord(params) {
    return axios.post(env.host.base + '/zx/collect/mergeRecord', params);
}
/**
 * 删除管理
 * @param {*} params 
 * @returns 
 */
export function deleteNote(params) {
    return axios.post(env.host.base + '/zx/collect/deleteNote', params);
}

/**
 * 采集管理
 * @param {*} params 
 * @returns 
 */
export function getMyData(params) {
    // return axios.get(env.host.base + '/zx/collect/record', params);
    // return axios.post(env.host.base + '/zx/taskConfig/save', params);

    return axios.get(env.host.base + '/zx/collect/getMyData', { params: params });
    // return axios({
    //     method: 'get',
    //     url: env.host.base + '/zx/collect/record',
    //     params: params
    //   });
}

/**
 * 根据DOCID获取标记列表
 * @param {*} params 
 * @returns 
 */
export function getMarksByDocId(params) {
    return axios.get(env.host.base + '/zx/collect/getMarksByDocId', { params: params });
}

/**
 * 参数数据分页
 * @param {*} params 
 * @returns 
 */
export function paramPage(params) {
    return axios.get(env.host.base + '/zx/recordParam/page', { params: params });
}

export function updateParam(params) {
    return axios.get(env.host.base + '/zx/recordParam/updateParam', { params: params });
}

/**
 * 创建
 * @param {*} params 
 * @returns 
 */
export function createParam(params) {
    return axios.get(env.host.base + '/zx/recordParam/createParam', { params: params });
  
}

/**
 * 删除管理
 * @param {*} params 
 * @returns 
 */
export function deleteParam(params) {
    return axios.post(env.host.base + '/zx/recordParam/deleteParam', params);
}

/** ===chatGPT====start */
/**
 * chatGPT page
 * @param {*} params 
 * @returns 
 */
export function chatGPTPage(params) {
    return axios.get(env.host.base + '/zx/chatGPT/page', { params: params });
}
/**
 * 导出采集数据分页
 * @param {*} params 
 * @returns 
 */
export function exportChatGPTTxt(params) {
    return axios({
        method: 'get',
        url: env.host.base + '/zx/chatGPT/exportChatGPTTxt',
        responseType: 'blob',
        params: params
      });

}

/**
 * 删除管理
 * @param {*} params 
 * @returns 
 */
export function deleteChatGPT(params) {
    return axios.post(env.host.base + '/zx/chatGPT/deleteChatGPT', params);
}

/** ===chatGPT====end */
