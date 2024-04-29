<!-- search -->
<template>
  <div class="search-box">
    <div class="search-top" v-if="state.dataList.length">
      <div v-if="isMobile" class="">
        <el-input v-model="state.textAnno" style="min-width: 250px" placeholder="请输入检索内容">
          <template #prepend>
            <el-select style="width: 80px" v-model="state.queryType">
              <el-option v-for="item in state.queryTypeOptions" :key="item.value" :label="item.label"
                :value="item.value" />
            </el-select>
          </template>
          <template #append>
            <el-button :icon="Search" @click="getList" />
          </template>
        </el-input>
      </div>
      <div v-else style="display: flex;-width: 600px;align-items: center;">
        <tj-select v-model="state.queryType" style="height: 50px;width: 120px;"
          :options="state.queryTypeOptions"></tj-select>
        <tj-input v-model:value="state.textAnno" placeholder="请输入检索内容" size="large" />
        <tj-button style="margin-left: 16px;" theme="primary" @click="getList">搜索</tj-button>
      </div>

    </div>
    <div class="search-bottom" v-loading="state.loading" v-if="state.queryType === 1">
      <div class="search-item" v-for="(item, index) in state.dataList" :key="item.id">
        <p class="search-item-title">{{ index + 1 }}.</p>
        <div class="sub-item">
          <div class="item-sm" v-for="(list, index) in item?.pictureAnnos" :key="index">
            <p class="w-[80%]">
              <tj-ellipsis :line="1" :content="list.textAnno" :style="{ maxWidth: isMobile ? '180px' : '100%' }">
                <span v-html="list.textAnno"></span> </tj-ellipsis>
            </p>
            <p class="flex">
              <TjImage :src="IMG_BASE_URL + list.picFileName" fit="contain" style="width: 50px" overlay-trigger="hover">
                <template #overlayContent>
                  <div style="
                    background: rgba(0, 0, 0, 0.4);
                    color: #fff;
                    height: 100%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                  " @click="() => {
                    state.visible = true;
                    state.imagesView = [IMG_BASE_URL + list?.picFileName];
                  }
                    ">
                    预览
                  </div>
                </template>
              </TjImage>
              <TjButton @click="detailsView(item.id)" class="!ml-4">
                编辑
              </TjButton>
            </p>

          </div>
        </div>
      </div>

    </div>
    <div class="search-water-item" v-else>
      <div class="flex" v-for="(item, index) in  state.dataList " :key="item.id">
        <div class="item-sm" v-for="(list, idx) in item?.pictureAnnos" :key="idx">
          <TjImage :src="IMG_BASE_URL + list.picFileName" fit="contain"
            style="width: auto;height: 200px; margin-right: 10px;" overlay-trigger="hover">
            <template #overlayContent>
              <div style="
                    background: rgba(0, 0, 0, 0.4);
                    color: #fff;
                    height: 100%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                  " @click="() => {
                    state.visible = true;
                    state.imagesView = [IMG_BASE_URL + list?.picFileName];
                  }
                    ">
                预览
              </div>
            </template>
          </TjImage>
        </div>
      </div>
    </div>
    <div class="search-pagination" v-if="state.dataList.length">
      <tj-pagination v-if="!isMobile" v-model="state.pageNo" v-model:pageSize="state.pageSize" :total="state.total"
        @current-change="onCurrentChange" @page-size-change="onPageSizeChange" />
      <tj-pagination v-else theme="simple" v-model="state.pageNo" v-model:pageSize="state.pageSize" :total="state.total"
        :total-content="false" :pageSizeOptions="[]" @current-change="onCurrentChange"
        @page-size-change="onPageSizeChange" />
    </div>

    <div :class="isMobile ? 'mt-[-150%]' : 'search-popup'" v-if="!state.dataList.length">
      <div v-if="isMobile" class="">
        <el-input v-model="state.textAnno" style="min-width: 250px" placeholder="请输入检索内容">
          <template #prepend>
            <el-select style="width: 80px" v-model="state.queryType">
              <el-option v-for="item in state.queryTypeOptions" :key="item.value" :label="item.label"
                :value="item.value" />
            </el-select>
          </template>
          <template #append>
            <el-button :icon="Search" @click="getList" />
          </template>
        </el-input>
      </div>
      <div class="pop-sear" v-else>
        <tj-select size="large" style="height: 50px;width: 120px;" v-model="state.queryType"
          :options="state.queryTypeOptions"></tj-select>
        <tj-input v-model:value="state.textAnno" placeholder="请输入检索内容" size="large" />
        <tj-button style="margin-left: 16px;" theme="primary" @click="getList">搜索</tj-button>
      </div>
      <div class="no-list" v-show="state.isShowTip">暂时没有检索出内容呢</div>
    </div>
  </div>
  <tj-image-viewer v-model:visible="state.visible" :images="state.imagesView" :draggable="false" />
</template>
<script lang="tsx">
export default {
  name: 'Search',
};
</script>
<script setup lang="tsx">
import { onMounted, reactive, onActivated } from 'vue';
import {
  TjInput,
  TjButton,
  TjPagination,
  TjEllipsis,
  TjImageViewer,
  TjImage,
} from 'tj-design-vue';
import { v4 as uuidv4 } from 'uuid';
import { Search } from '@element-plus/icons-vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router';
import { search } from '@/api/list';
const IMG_BASE_URL = 'https://www.opencast.site/zx/image/';
const onCurrentChange = (index, pageInfo) => {
  state.pageNo = index;
  localStorage.setItem('pageParams', JSON.stringify({
    pageNo: state.pageNo,
    pageSize: state.pageSize
  }))
  getList();
};
onActivated(() => {
  console.log('缓存');
})
onMounted(() => {
  if (localStorage.getItem('pageParams')) {
    state.textAnno = localStorage.getItem('searchText') || ''
    state.pageSize = JSON.parse(localStorage.getItem('pageParams') || '')?.pageSize
    state.pageNo = JSON.parse(localStorage.getItem('pageParams') || '')?.pageNo
    getList();
  }
})
const router = useRouter();
const onPageSizeChange = size => {
  state.pageNo = 1;
  state.pageSize = size;
  localStorage.setItem('pageParams', JSON.stringify({
    pageNo: state.pageNo,
    pageSize: state.pageSize
  }))
  getList();
};
const isMobile = localStorage.getItem('isMobile') || false;
const state = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
  status: 1,
  textAnno: '',
  loading: false,
  dataList: [] as any,
  isShowTip: false,
  imagesView: [] as any,
  visible: false,
  queryType: 1,
  queryTypeOptions: [
    { label: "文字", value: 1 },
    { label: "图片", value: 2 },
  ]
});
const resetPage = () => {
  state.pageNo = 1;
  state.pageSize = 10;
  localStorage.setItem('pageParams', JSON.stringify({
    pageNo: state.pageNo,
    pageSize: state.pageSize
  }))
};
const getList = async () => {
  state.loading = true;
  try {
    const resData = await search({
      pageNo: state.pageNo,
      pageSize: state.pageSize,
      textAnno: state.textAnno,
      status: state.status,
      queryType: state.queryType
    });
    localStorage.setItem('searchText', state.textAnno)
    if (resData.status === 0) {
      // pageParams.count = resData.map.count;
      state.dataList = resData.list;
      // state.dataList.length &&
      //   state.dataList.forEach(a => {
      //     a.pictureAnnos.forEach(j => {
      //       j.textAnno = j.textAnno.replaceAll(
      //         state.textAnno,
      //         `<span style="color: #ff5252;">${state.textAnno}</span>`,
      //       );
      //     });
      //   });
      state.total = resData.map.count;
      state.isShowTip = !state.dataList.length;
    } else {
      state.dataList = [];
      resetPage();
    }
  } catch (error) {
    state.dataList = [];
    resetPage();
    console.log(error);
  } finally {
    state.loading = false;
  }
};

const detailsView = id => {
  localStorage.setItem('id', id)
  router.push({
    name: 'Details',
    query: {
      id,
    },
  });
};

onBeforeRouteLeave((to, from) => {
  if (to.name !== 'Details') {
    localStorage.removeItem('pageParams');
    localStorage.removeItem('scroll');
  }
})
</script>
<style lang="less" scoped>
.search-box {
  width: 94%;
  // height: 94%;
  height: calc(100vh - 130px);
  max-height: calc(100vh - 130px);
  overflow-y: auto;
  overflow-x: auto;
  position: relative;
  margin: 0 auto;
  border-radius: 4px;
  background: #fff;
  padding: 16px;
  margin-top: 10px;

  .search-top {

    // grid-gap: 16px;
    margin: 0 auto;

    .tj-input__wrap {
      width: 550px;

      ::v-deep(.tj-size-l) {
        height: 50px;
        border: 2px solid #c4c7ce;
      }
    }

    .tj-select__wrap {
      ::v-deep(.tj-input) {
        height: 50px;
        border: 2px solid #c4c7ce;
        border-right: none;
        border-radius: 4px 0 0 4px;
      }

    }

    .tj-button {
      width: 108px;
      height: 50px;
      background: #4e6ef2;
      color: #fff;
    }
  }

  .search-water-item {
    display: flex;
    flex-wrap: wrap;
    margin-top: 16px;
    height: calc(100% - 148px);
    padding: 16px;
    border-radius: 8px;
    overflow-y: scroll;

    // justify-content: center;
    align-content: flex-start;

    .item-sm {
      height: 200px;
      width: auto;
      margin-bottom: 8px;

      img {
        height: 120px;
        width: auto;
        margin-right: 8px;
      }
    }
  }

  .search-bottom {
    margin-top: 16px;
    height: calc(100vh - 290px);
    padding: 16px;
    border-radius: 8px;
    overflow-y: auto;
    background: #fff;



    .search-item {
      display: flex;

      .search-item-title {
        padding-right: 16px;
      }

      .sub-item {
        width: 100%;

        .item-sm {
          width: 98%;
          display: flex;
          margin-bottom: 16px;
          grid-gap: 16px;
          justify-content: space-between;
        }
      }
    }
  }

  .search-pagination {
    padding-top: 16px;
  }

  .tj-input__wrap {
    width: 550px;

    ::v-deep(.tj-size-l) {
      height: 50px;
      border: 2px solid #4569ff;
      border-radius: 0 4px 4px 0;
    }
  }


  .search-popup {
    // position: absolute;
    // left: 50%;
    // top: 30%;
    transform: translate(-50%, -50%);
    margin-top: -30%;
    margin-left: 50%;
    max-width: 800px;

    .tj-select__wrap {
      ::v-deep(.tj-input) {
        height: 50px;
        border: 2px solid #4569ff;
        border-right: none;
        border-radius: 4px 0 0 4px;
      }

    }


    .pop-sear {
      display: flex;
      align-items: center;
      // grid-gap: 16px;



      .tj-button {
        width: 108px;
        height: 50px;
        background: #4e6ef2;
        color: #fff;
      }
    }

    .no-list {
      margin-top: 16px;
    }
  }
}
</style>
<style lang="less">
.tj-image-viewer__utils {
  display: none !important;
}
</style>
