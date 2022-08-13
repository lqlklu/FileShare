<script lang="ts" setup>
import { ElHeader, ElSwitch } from "element-plus";
import { Moon, Sunny } from "@element-plus/icons-vue";
import { useDark, useToggle } from "@vueuse/core";

const isDark = useDark();
const toggleDark = useToggle(isDark);
</script>

<template>
  <ElHeader class="header">
    <div class="title">File Share</div>
    <div class="content">
      <ElSwitch :model-value="isDark" @change="toggleDark()" inline-prompt :active-icon="Moon" :inactive-icon="Sunny" />
    </div>
  </ElHeader>
</template>

<style scoped>
.header {
  border-bottom: solid 1px var(--el-menu-border-color);
  display: flex;
  flex-direction: row;
  align-items: center;
}
span.title {
  margin-right: 20px;
}
.content {
  flex-grow: 1;
  display: flex;
  justify-content: flex-end;
}
</style>
