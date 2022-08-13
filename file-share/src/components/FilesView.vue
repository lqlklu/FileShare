<script lang="ts" setup>
import { ElBreadcrumb, ElBreadcrumbItem, ElLink, ElContainer, ElScrollbar, ElButton, ElSwitch } from "element-plus";
import { Refresh, Hide, View, Plus } from "@element-plus/icons-vue";

import TreeItemView from "@/components/TreeItem.vue";
import { useFilesStore } from "@/stores/files";

const files = useFilesStore();
files
  .fetch()
  .then(() => {})
  .catch((err) => {
    console.error(err);
  });
</script>

<template>
  <div class="files">
    <ElContainer class="files-container" direction="vertical">
      <div class="control-bar">
        <div class="path">
          <ElBreadcrumb separator="/">
            <ElBreadcrumbItem v-for="it in files.pathArr">
              <ElLink v-if="files.path != it.dest" @click="files.fetch(it.dest)">
                {{ it.name }}
              </ElLink>
              <span v-if="files.path == it.dest">
                {{ it.name }}
              </span>
            </ElBreadcrumbItem>
          </ElBreadcrumb>
        </div>
        <div class="buttons">
          <ElButton v-if="files.ok" :icon="Plus" circle text />
          <ElSwitch v-if="files.ok" v-model="files.showHidden" inline-prompt :active-icon="View" :inactive-icon="Hide" />
        </div>
      </div>
      <div class="items">
        <ElButton v-if="files.error" @click="files.fetch()" type="danger" :icon="Refresh">Refresh</ElButton>
        <ElScrollbar v-show="!files.fetching && !files.error">
          <TreeItemView v-for="it in files.items" :it="it" />
        </ElScrollbar>
      </div>
    </ElContainer>
  </div>
</template>

<style scoped>
.files {
  height: 100%;
  display: flex;
}
.files-container {
  flex-direction: column;
  overflow: visible;
}
.control-bar {
  padding: 0.6rem 1rem;
  border-bottom: solid 1px var(--el-menu-border-color);
  display: flex;
  flex-direction: row;
  align-items: center;
}
.control-bar .path {
  flex-grow: 1;
}
.control-bar .buttons {
  flex-grow: 0;
}
.buttons .el-switch {
  margin-left: 0.5rem;
}
.items {
  height: 100px;
  flex: 1 1 0;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}
.items .el-scrollbar {
  width: 100%;
  flex-grow: 1;
}
</style>
