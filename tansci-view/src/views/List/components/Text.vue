<template>
  <div class="h-full item-content pr-6 pl-6">
    <template v-if="useData && useData.length">
      <div class="pt-4" v-for="item in useData" :key="item?.ptype">
        <div class="pb-2">{{ item?.objectAnno }}</div>
        <tj-textarea
          v-model="textValue"
          name="description"
          :autosize="{ minRows: 3, maxRows: 18 }"
        />
        <div class="flex justify-end mt-4">
          <tj-button @click="saveChange">保存修改</tj-button>
        </div>
      </div>
    </template>
    <tj-empty v-else class="pt-12" description="暂无数据" />
  </div>
</template>
<script lang="ts" setup name="Text">
  import { TjEmpty, TjTextarea, MessagePlugin } from 'tj-design-vue';
  import { ref, watch } from 'vue';
  import { useVModel } from '@vueuse/core';
  import { useRoute } from 'vue-router';
  import { updateTextAnno } from '@/api/list';
  const route = useRoute();

  const emits = defineEmits(['update:data']);
  const props = defineProps({
    data: {
      type: Array,
      default: () => [],
    },
  });
  const useData: any = useVModel(props, 'data', emits);
  let textValue = ref('');

  watch(
    () => useData.value,
    newVal => {
      console.log('newVal', newVal);

      textValue.value = newVal[0]?.textAnno;
    },
    { immediate: true, deep: true },
  );

  const saveChange = async () => {
    try {
      const res = await updateTextAnno({
        id: route.query.id,
        textAnno: textValue.value,
      });
      if (res.status === 0) {
        useData.value[0].textAnno = textValue.value;
        MessagePlugin.success('修改成功');
      } else {
        MessagePlugin.error('修改失败，请稍后再试！');
      }
    } catch (error) {
      MessagePlugin.error('修改失败，请稍后再试！');
    }
  };
</script>
<style lang="less" scoped></style>
