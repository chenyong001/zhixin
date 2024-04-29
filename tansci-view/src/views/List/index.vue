<template>
  <div class="list-container">
    <div class="flex justify-end mb-4 header-container"> </div>

    <div class="mb-2 body-container" v-loading="loading" style="height: 94%">
      <tj-table ref="tableRef" v-show="data?.length > 0" row-key="index" :columns="columns" :data="data || []" hover
        :maxHeight="!isMobile ? 'calc(100vh - 200px)' : 'calc(100vh - 300px)'" />
      <div v-show="data?.length === 0" class="flex items-center justify-center" style="height: 70%">
        <tj-empty description="暂无数据~" />
      </div>
    </div>
    <tj-pagination v-if="!isMobile" v-model="pageParams.current" v-model:pageSize="pageParams.pageSize"
      :total="pageParams.count" :showPageSize="true" @page-size-change="onPageSizeChange"
      @current-change="onCurrentChange" />
    <tj-pagination v-else theme="simple" :total-content="false" v-model="pageParams.current"
      v-model:pageSize="pageParams.pageSize" :total="pageParams.count" :showPageSize="true"
      @page-size-change="onPageSizeChange" @current-change="onCurrentChange" />
    <tj-image-viewer v-model:visible="visible" :images="imagesView" :draggable="false" />
  </div>
</template>

<script setup lang="tsx" name="Home">
import { ref, reactive, onMounted, nextTick } from 'vue';
import { useRouter, onBeforeRouteLeave } from 'vue-router';
import { TjTable, TjImage, TjImageViewer, TjButton, TjPagination, } from 'tj-design-vue';
import { formatToDateTime } from '../../utils/utils.js';
// import ListMockData from './mock/list.json';
import { search } from '../../api/list';
const IMG_BASE_URL = 'https://www.opencast.site/zx/image/';
const data = ref<any>(null);
const router = useRouter();
const loading = ref(true);
const visible = ref(false);
const imagesView: any = ref([]);
const isMobile = localStorage.getItem('isMobile') || false
const pageParams = reactive(localStorage.getItem('pageParams') ? JSON.parse(localStorage.getItem('pageParams') || '') : {
  current: 1,
  pageSize: 10,
  count: 0,
});
const tableRef = ref(null)
const columns = [
  {
    colKey: 'id',
    title: '序号',
    width: '120',
  },
  {
    colKey: '',
    title: '图片',
    cell: (h, { row }) => {
      return (
        <div
          class="cursor-pointer"
          onClick={() => {
            visible.value = true;
            imagesView.value[0] = {
              mainImage: `${IMG_BASE_URL}${row.picture}`,
              download: false,
            };
          }}
        >
          <TjImage
            src={`${IMG_BASE_URL}${row.picture}`}
            fit="contain"
            style={{
              width: '100px',
              height: '48px',
            }}
            overlay-content={renderMask}
            overlay-trigger="hover"
          />
        </div>
      );
    },
    width: '210',
  },
  // {
  //   colKey: 'status',
  //   title: '审核状态',
  //   width: '150',
  //   cell: (h, { row }) => {
  //     return (
  //       <TjTag theme={row.status ? 'success' : 'warning'} variant="light">
  //         {row.status ? '审核通过' : '待审核'}
  //       </TjTag>
  //     );
  //   },
  // },
  // {
  //   colKey: 'createTime',
  //   title: '创建时间',
  //   width: '200',
  //   cell: (h, { row }) => {
  //     return <span>{formatToDateTime(row.createTime)}</span>;
  //   },
  // },
  // {
  //   colKey: 'updateTime',
  //   title: '更新时间',
  //   width: '200',
  //   cell: (h, { row }) => {
  //     return <span>{formatToDateTime(row.createTime)}</span>;
  //   },
  // },
  {
    colKey: 'picture',
    title: '文件名',
    width: '200',
  },
  {
    colKey: 'opt',
    title: '操作',
    width: '200',
    cell: (h, { row }) => {
      return (
        <div class="cursor-pointer">
          <TjButton
            onClick={() => {
              detailsView(row);
            }}
          >
            详情
          </TjButton>
        </div>
      );
    },
  },
];
onMounted(() => {
  nextTick(() => {
    let box = document.querySelector(".tj-table__content")
    setTimeout(() => {
      if (box) {
        box.scrollTop = Number(localStorage.getItem('scroll'))
      }
    }, 1000);
  })

})
const onCurrentChange = (index, pageInfo) => {
  pageParams.current = pageInfo.current;
  localStorage.setItem('pageParams', JSON.stringify(pageParams))
  getList();
};
const onPageSizeChange = size => {
  pageParams.current = 1;
  pageParams.pageSize = size;
  localStorage.setItem('pageParams', JSON.stringify(pageParams))
  getList();
};

const renderMask = () => (
  <div
    style={{
      background: 'rgba(0,0,0,.4)',
      color: '#fff',
      height: '100%',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    }}
  >
    预览
  </div>
);

const detailsView = row => {
  localStorage.setItem('id', row.id)
  let box = document.querySelector(".tj-table__content")
  localStorage.setItem('scroll', box?.scrollTop + '' || "0")

  router.push({
    name: 'Details',
    query: {
      id: row.id,
    },
  });
};

const resetPage = () => {
  pageParams.count = 0;
  pageParams.current = 1;
  pageParams.pageSize = 10;
};

const getList = async () => {
  loading.value = true;
  try {
    const resData: any = await search({
      pageNo: pageParams.current,
      pageSize: pageParams.pageSize,
    });

    if (resData.status === 0) {
      pageParams.count = resData.map.count;
      data.value = resData.list;
    } else {
      data.value = [];
      resetPage();
    }
  } catch (error) {
    data.value = [];
    resetPage();
    console.log(error);
  } finally {
    loading.value = false;
  }
};

getList();
onBeforeRouteLeave((to, from) => {
  if (to.name !== 'Details') {
    localStorage.removeItem('pageParams');
    localStorage.removeItem('scroll');
  }
})
</script>

<style lang="less" scoped>
.list-container {
  margin: 0 24px;
  // height: 97%;
  max-height: calc(100vh - 115px);
  overflow-y: auto;
  background: #fff;
  padding: 16px;
  border-radius: 4px;
}
</style>
<style lang="less">
.tj-image-viewer__utils {
  display: none !important;
}
</style>
