<script lang="ts" setup>
import { ref } from "vue";
import { ElBreadcrumb, ElBreadcrumbItem, ElLink, ElContainer, ElScrollbar, ElButton, ElSwitch, ElUpload, UploadProps, ElDialog, ElMessage, UploadProgressEvent, UploadRequestOptions } from "element-plus";
import { UploadAjaxError } from "element-plus/es/components/upload/src/ajax";
import { Refresh, Hide, View, Plus, UploadFilled, Close, Delete, Download } from "@element-plus/icons-vue";

import TreeItemView from "@/components/TreeItem.vue";
import MyUpload from "@/components/Upload.vue";
import { useFilesStore } from "@/stores/files";

export interface FilesViewProps {
  path: string;
}

const props = defineProps<FilesViewProps>();

const files = useFilesStore();
files
  .navigate(props.path)
  .then(() => {})
  .catch((err) => {
    console.error(err);
  });
const showUploadDialog = ref(false);
const onSuccess = () => {
  console.log("upload success");
  ElMessage.success("upload success");
  files.refresh();
};
const onError = () => {
  console.log("upload error");
  ElMessage.error("upload error");
};

const clearSelect = () => {
  files.clearSelect();
};
const deleteSelected = () => {
  files.deleteSelected().then(() => {
    files.refresh();
  });
};
const downloadSelected = () => {
  files.downloadSelected().then(() => {
    console.log("download selected ok");
  });
};
</script>

<template>
  <div class="files">
    <ElContainer class="files-container" direction="vertical">
      <div class="control-bar" v-if="!files.selecting">
        <div class="path">
          <ElBreadcrumb separator="/">
            <ElBreadcrumbItem v-for="it in files.pathArr">
              <ElLink v-if="files.path != it.dest" @click="files.navigate(it.dest)">
                {{ it.name }}
              </ElLink>
              <span v-if="files.path == it.dest">
                {{ it.name }}
              </span>
            </ElBreadcrumbItem>
          </ElBreadcrumb>
        </div>
        <ElButton :icon="Refresh" circle text @click.stop="files.refresh()"></ElButton>
        <div class="actions">
          <ElButton v-if="files.ok" :icon="Plus" circle text @click="showUploadDialog = true" />
          <ElDialog v-model="showUploadDialog">
            <MyUpload :action="`/api/files`" multiple drag :limit="10" :on-success="onSuccess" :on-error="onError">
              <div>Drop file here or <span> click to upload </span></div>
              <div>File upload will be put in <code>Download/FileShare</code></div>
            </MyUpload>
          </ElDialog>
          <ElSwitch v-if="files.ok" v-model="files.showHidden" inline-prompt :active-icon="View" :inactive-icon="Hide" />
        </div>
      </div>
      <div class="select-control-bar" v-if="files.selecting">
        <div class="actions">
          <ElButton :icon="Delete" circle text @click.stop.prevent="deleteSelected"></ElButton>
          <ElButton :icon="Download" circle text @click.stop="downloadSelected"></ElButton>
        </div>
        <ElButton :icon="Close" circle text @click.stop="clearSelect"></ElButton>
      </div>
      <div class="items">
        <ElButton v-if="files.error" @click="files.refresh()" type="danger" :icon="Refresh">Refresh</ElButton>
        <ElScrollbar v-show="!files.refreshing && !files.error">
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
.select-control-bar {
  padding: 0.6rem 1rem;
  border-bottom: solid 1px var(--el-menu-border-color);
  display: flex;
  flex-direction: row;
  align-items: center;
}

.select-control-bar .actions {
  flex-grow: 1;
  display: flex;
  flex-direction: row;
}

.control-bar .path {
  flex-grow: 1;
}

.control-bar .actions {
  flex-grow: 0;
}

.actions .el-switch {
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
