<template>
  <div class="!overflow-x-scroll list-detail-container">
    <div class="flex flex-col body-container min-w-[1100px]">
      <div class="flex justify-end mb-4">
        <tj-button theme="success" @click="approved" :disabled="processBtnText === '已完成审核'">{{
          processBtnText
        }}</tj-button>
      </div>

      <div class="detail-container">
        <tj-row :gutter="16">
          <tj-col v-for="(item, index) in  dataList " :key="item.id" :span="24 / dataList.length">
            <div class="h-96" :style="{ marginLeft: index % 2 ? 16 : 0 + 'px' }" v-if="!isMobile">
              <vue-photo-zoom-pro :url="IMG_BASE_URL + item.picFileName" :high-url="IMG_BASE_URL + item.picFileName"
                type="circle" :width="photoZoomWidth" :scale="3" @mousewheel="handleMouseWheel" />
            </div>
            <div class="h-50" :style="{ marginLeft: index % 2 ? 16 : 0 + 'px' }" v-else>
              <img :style="{ width: photoZoomWidth }" :src="IMG_BASE_URL + item.picFileName" alt="">
            </div>
          </tj-col>
        </tj-row>

        <tj-row class="deltail-content">
          <div class="deltail-box">
            <tj-col v-for="( item, i ) in  dataList " :key="item.id" :span="24 / dataList.length">
              <tj-checkbox-group v-model="checked[item.id]">
                <div class="checked-item" v-for="( list, index ) in  item.textAnno " :key="index">
                  <tj-checkbox :value="list" :disabled="!Boolean(list)">
                    <tj-input v-model:value="item.textAnno[index]" />
                    <tj-icon type="tj-add-one" style="margin-left: 16px" @click.prevent="addTextAnno(i, index)"></tj-icon>
                    <tj-icon type="tj-reduction-one" style="margin-left: 16px"
                      @click.prevent="delTextAnno(i, index)"></tj-icon>
                  </tj-checkbox>
                </div>
              </tj-checkbox-group>
            </tj-col>
          </div>
          <tj-button class="merge-btn" @click="mergeChange">合并</tj-button>
        </tj-row>

      </div>
      <div class="flex justify-end mt-4">
        <tj-button @click="saveChange">保存修改</tj-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="tsx" name="Details">
import { ref } from 'vue';
import { updateTextAnno } from '@/api/list';
import {
  MessagePlugin,
  TjInput,
  TjRow,
  TjCol,
  TjCheckbox,
  TjCheckboxGroup,
  TjIcon,
} from 'tj-design-vue';
import { useRoute, useRouter } from 'vue-router';
import { searchById, audit } from '@/api/list';
import vuePhotoZoomPro from 'vue-photo-zoom-pro';
import 'vue-photo-zoom-pro/dist/style/vue-photo-zoom-pro.css';
const IMG_BASE_URL = 'https://www.opencast.site/m2c/image/';
const dataList = ref([]) as any;
const route = useRoute();
const router = useRouter();
const processBtnText = ref('审核通过');
const checked = ref({}) as any;
const approved = async () => {
  try {
    const res = await audit(route.query.id);
    if (res.status === 0) {
      MessagePlugin.success('审核成功');
      processBtnText.value = '已完成审核';
      setTimeout(() => {
        router.push('/list');
      }, 500);
    } else {
      MessagePlugin.error('审核失败，请稍后再试！');
    }
  } catch (error) {
    MessagePlugin.error('审核失败，请稍后再试！');
  }
};
// 监听绑定
let isMobile = localStorage.getItem('isMobile') || false;
const addTextAnno = (i, index) => {
  dataList.value[i].textAnno.splice(index + 1, 0, '');
};
const delTextAnno = (i, index) => {
  dataList.value[i].textAnno.splice(index, 1);
};
const mergeChange = () => {
  let arr: any = []
  for (let key in checked.value) {
    if (checked.value[key].length) {
      let index = dataList.value.findIndex(a => a.id === key);
      let str = '';
      let arr: any = []
      // str = checked.value[key].join(' ');
      checked.value[key].forEach((a, j) => {
        let titleIndex = dataList.value[index].textAnno.indexOf(a);
        arr.push({ id: titleIndex, index: index, text: dataList.value[index].textAnno[titleIndex] });
        if (titleIndex !== -1) {
          if (j !== 0) {
            dataList.value[index].textAnno.splice(titleIndex, 1);
          }
        }
      });
      arr.sort(function (a, b) {
        return a.id - b.id
      })
      arr.map((item) => {
        str += item.text + '  '
      })
      arr.forEach((item, index) => {
        if (item !== -1) {
          if (index === 0) {
            dataList.value[item.index].textAnno.splice(item.id, 1, str);
          }
        }
      })
      // str = checked.value[key].join(' ');
      // console.log(index);
      // checked.value[key].forEach((a, j) => {
      //   let titleIndex = dataList.value[index].textAnno.indexOf(a);
      //   console.log(dataList.value[index].textAnno.indexOf(a));
      //   arr.push(titleIndex);
      //   if (titleIndex !== -1) {
      //     if (j === 0) {
      //       dataList.value[index].textAnno.splice(titleIndex, 1, str);
      //     } else {
      //       dataList.value[index].textAnno.splice(titleIndex, 1);
      //     }
      //   }
      // });
      checked.value[key] = [];
    }
  }
};

// 鼠标滚动
const photoZoomWidth = ref(166);
const handleMouseWheel = e => {
  const index = 10;
  if (photoZoomWidth.value >= 300) {
    if (e.deltaY < 0) {
      photoZoomWidth.value -= index;
    }
    return;
  }
  if (photoZoomWidth.value <= 80) {
    if (e.deltaY > 0) {
      photoZoomWidth.value += index;
    }
    return;
  }
  e.deltaY > 0 ? (photoZoomWidth.value += index) : (photoZoomWidth.value -= index);
};
const getDetailsData = async () => {
  try {
    const resDetailsData = await searchById({ id: localStorage.getItem('id') });
    if (resDetailsData.status === 0) {
      dataList.value = resDetailsData.data.pictureAnnos;
      resDetailsData.data.pictureAnnos.forEach((a, i) => {
        checked.value[a.id] = [];
        dataList.value[i].textAnno = a.textAnno.split('\n');
      });
    } else {
      dataList.value = [];
    }
  } catch (error) {
    dataList.value = [];
    console.log(error);
  }
};
const saveChange = async () => {
  try {
    let subList = JSON.parse(JSON.stringify(dataList.value));
    subList.forEach(a => {
      a.textAnno = a.textAnno.join('\n');
    });
    let aaa = new FormData();
    aaa.append('pid', String(route.query.id));
    aaa.append('pictureAnnos', JSON.stringify(subList));
    const res = await updateTextAnno(aaa);
    if (res.status === 0) {
      MessagePlugin.success('修改成功');
      setTimeout(() => {
        router.push('/list');
      }, 500);
    } else {
      MessagePlugin.error('修改失败，请稍后再试！');
    }
    // }, 500);
  } catch (error) {
    console.log(error);

    MessagePlugin.error('修改失败，请稍后再试！');
  }
};
getDetailsData();
</script>

<style lang="less" scoped>
.list-detail-container {
  margin: 0 24px;
  max-height: calc(100vh - 115px);
  // height: 96%;
  // height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #fff;
  padding: 16px;
  border-radius: 4px;
}


::v-deep(.tj-tabs__content) {
  // height: 78vh;
  overflow-y: auto;
  padding-bottom: 24px;
}

.deltail-content {
  margin: 24px 0;
  position: relative;
  background: #fff;

  .deltail-box {
    height: 300px;
    overflow-y: auto;
    flex: 1;
    list-style: none;
    display: flex;
    flex-flow: row wrap;
  }

  ::v-deep(.tj-checkbox-group) {
    display: block;

    .checked-item {
      padding: 8px;
      border: 1px solid #dee0e3;

      .tj-checkbox {
        width: 90%;

        .tj-checkbox__label {
          width: 100%;
          display: flex;
          align-items: center;
        }
      }

      .tj-input {
        border-color: transparent;
      }
    }
  }

  .merge-btn {
    position: absolute;
    right: 20px;
    top: -45px;
  }
}
</style>
<style lang="less">
.vue-photo-zoom-pro {
  height: 100%;

  // width: 100%;
  .img-preview {
    max-height: 100%;
  }
}
</style>
