import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useAppStore = defineStore('app', () => {
  const isCollapse = ref<boolean>(false);

  const toggleCollapse = () => {
    isCollapse.value = !isCollapse.value;
  };

  const setCollapse = (value: boolean) => {
    isCollapse.value = value;
  };

  return {
    isCollapse,
    toggleCollapse,
    setCollapse,
  };
});
