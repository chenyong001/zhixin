export default {
    delayLoadJS,
    delayLoadCSS,
}

/**
 * 加载js
 * @param {*} path
 */
function delayLoadJS(path) {
    if (!path) {
        return Promise.reject()
    }
    return new Promise((resolve, reject) => {
        let dom = null
        const scripts = document.querySelectorAll('script')
        for (const item of scripts) {
            if (new RegExp(path).test(item.src)) {
                dom = item
                onload(dom, () => resolve())
                return
            }
        }
        if (!dom) {
            const script = document.createElement('script')
            script.src = path
            script.onload = () => {
                resolve()
                script.onload = null
            }
            script.onerror = () => reject()
            document.body.appendChild(script)
        }
    })
}

/**
 * 加载css
 * @param {*} path
 */
function delayLoadCSS(path) {
    if (!path) {
        return Promise.reject()
    }
    return new Promise((resolve, reject) => {
        let dom = null
        const links = document.querySelectorAll('link')
        for (const item of links) {
            if (new RegExp(path).test(item.href)) {
                dom = item
                onload(dom, resolve)
                return
            }
        }
        if (!dom) {
            const link = document.createElement('link')
            link.rel = 'stylesheet'
            link.href = path
            link.onload = () => {
                resolve()
                link.onload = null
            }
            link.onerror = () => reject()
            document.head.appendChild(link)
        }
    })
}

/**
 * 避免同时加载
 * @param {*} dom
 * @param {*} resolve
 */
function onload(dom, resolve) {
    const oldOnload = dom.onload
    if (oldOnload) {
        dom.onload = () => {
            oldOnload()
            resolve()
        }
    } else {
        resolve()
    }
}
