<script lang="ts" setup>
import { ref } from "vue";
import { ElLink, ElMenuItem, ElIcon, ElLoading } from "element-plus";
import { Folder, Document } from "@element-plus/icons-vue";

import { TreeItem, navigateFile } from "@/api/tree";
import { useFilesStore } from "@/stores/files";
import path from "@/utils/path";
import { humanSize } from "@/utils/common";

export interface TreeItemProps {
  it: TreeItem;
}
const props = defineProps<TreeItemProps>();
const lengthText = humanSize(props.it.length);
const files = useFilesStore();
const click = () => {
  if (props.it.type == "dir") {
    files
      .fetch(path.join(files.path, props.it.name))
      .then(() => {})
      .catch((err) => {
        console.error(err);
      });
  } else {
    navigateFile(path.join(files.path, props.it.name));
  }
};
</script>

<template>
  <div class="tree-item" @click="click" :key="props.it.path">
    <ElIcon class="icon" :size="26">
      <Document v-if="props.it.type != 'dir'" />
      <Folder v-if="props.it.type == 'dir'" />
    </ElIcon>
    <div class="info">
      <div class="name">{{ props.it.name }}</div>
      <div class="length">{{ lengthText }}</div>
      <div class="last-modify">{{ new Date(props.it.lastModify).toLocaleString() }}</div>
    </div>
  </div>
</template>

<style scoped>
.tree-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem 1.2rem;
  font-size: var(--el-menu-item-font-size);
  cursor: pointer;
}
.tree-item:hover {
  background-color: var(--el-menu-hover-bg-color);
}
.tree-item .icon {
  margin-right: 20px;
}
.info {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
.info > div {
  width: 100%;
  flex: 1 0 0;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
