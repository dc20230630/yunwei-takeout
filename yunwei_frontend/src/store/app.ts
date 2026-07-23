import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useAppStore = defineStore('app', () => {
  const isCollapse = ref<boolean>(false);
  const shopStatus = ref<number>(1);

  const toggleCollapse = () => {
    isCollapse.value = !isCollapse.value;
  };

  const setCollapse = (value: boolean) => {
    isCollapse.value = value;
  };

  const setShopStatus = (value: number) => {
    shopStatus.value = value;
  };

  return {
    isCollapse,
    shopStatus,
    toggleCollapse,
    setCollapse,
    setShopStatus,
  };
});
