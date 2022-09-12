<script lang="ts" setup>
import { ref } from "vue";
import { ElLink, ElMenuItem, ElIcon, ElLoading } from "element-plus";
import { Folder, Document } from "@element-plus/icons-vue";

import { TreeItem, navigateFile } from "@/api/files";
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
  if (files.selecting) {
    files.toggleSelect(props.it.path);
  } else {
    if (props.it.type == "dir") {
      files.navigate(props.it.path);
    } else {
      navigateFile(path.join(files.path, props.it.name));
    }
  }
};
const select = () => {
  files.toggleSelect(props.it.path);
};
</script>

<template>
  <div class="tree-item" @click="click" :class="{ selected: files.selected(props.it.path) }">
    <ElIcon class="icon" :size="26" @click.stop="select">
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
.tree-item.selected {
  background-color: rebeccapurple;
}
.tree-item:hover {
  background-color: var(--el-menu-hover-bg-color);
}
.tree-item.selected:hover {
  background-color: purple;
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
  height: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
}
.name {
  flex: 3 0 0;
  text-overflow: ellipsis;
}
.length {
  flex: 1 0 0;
}
.last-modify {
  flex: 1 0 0;
}
</style>
