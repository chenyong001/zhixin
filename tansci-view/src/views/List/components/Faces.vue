<template>
  <div class="item-content pr-6 pl-6">
    <div class="pt-4" v-for="item in useData" :key="item?.ptype">
      <div class="pb-2">{{ item?.objectAnno }}</div>
      <tj-progress
        style="width: 100%"
        theme="plump"
        color="#3296fa"
        :percentage="formatScore(item.score)"
      />
    </div>
    <tj-empty v-show="!useData || !useData.length" class="pt-12" description="暂无数据" />
  </div>
</template>
<script lang="ts" setup name="Faces">
  import { TjProgress } from 'tj-design-vue';

  import { useVModel } from '@vueuse/core';
  const emits = defineEmits(['update:data']);
  const props = defineProps({
    data: {
      type: Array,
      default: () => [],
    },
  });
  const useData: any = useVModel(props, 'data', emits);
  const formatScore = (score: String) => {
    let scoreNum = Number(score) * 100;
    scoreNum = Number(scoreNum.toFixed(2));
    return scoreNum;
  };
</script>
<style lang="less" scoped></style>
