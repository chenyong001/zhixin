<template>
  <el-config-provider :locale="locale">
    <keep-alive>
      <router-view></router-view>
    </keep-alive>
  </el-config-provider>
</template>
<script setup>
import { reactive, toRefs } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const state = reactive({
  locale: zhCn
})

const { locale } = toRefs(state)
// 判断手机端还是PC端
const _isMobile = () => {
  let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
  flag ? localStorage.setItem('isMobile', flag) : localStorage.removeItem('isMobile')
  return flag;
}
console.log(_isMobile());
</script>
<style>
:root {
  /* 颜色 */
  --theme: #242e42;

  /* 背景色 */
  --bg1: #eff4f9;

  /* 文字颜色 */
  --t1: #fff;
  --t2: #f4f4f4;
  --t3: #ededed;
  --t4: #d8d8d8;
  --t5: #b3b3b3;
  --t6: #999;
  --t7: #666;
  --t8: #3d3d3d;
  --t9: #181818;

  /* 操作颜色 */
  --delete: #f56c6c;
  --edit: #0084ff;
  --add: #63ba4d;
  --query: #909399;
  --down: #E6A23C;
  --upper: #67C23A;
  --role: #006000;
}

* {
  box-sizing: border-box;
}

html,
body {
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
  overflow: hidden;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  font-size: 14px;
}

#nprogress .bar {
  /*自定义进度条颜色*/
  background: var(--theme) !important;
}
</style>
