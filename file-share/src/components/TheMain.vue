<script lang="ts" setup>
import { ElContainer, ElHeader, ElAside, ElMain, ElMenu, ElMenuItem, ElButton } from "element-plus";
import { RouterView, useRoute } from "vue-router";

import TheHeader from "@/components/TheHeader.vue";

const route = useRoute();
const handleOpen = (key: string) => {};
const handleClose = (key: string) => {};
</script>

<template>
  <ElContainer class="container" direction="vertical">
    <TheHeader />
    <ElContainer class="main" direction="horizontal">
      <ElAside width="200px">
        <ElMenu class="menu" :default-active="route.path" @open="handleOpen" @close="handleClose" :router="true">
          <ElMenuItem index="/">
            <span>Home</span>
          </ElMenuItem>
          <ElMenuItem index="/files">
            <span> Files </span>
          </ElMenuItem>
          <ElMenuItem index="/documents">
            <span> Documents </span>
          </ElMenuItem>
          <ElMenuItem index="/downloads">
            <span> Downloads </span>
          </ElMenuItem>
          <ElMenuItem index="/music">
            <span> Music </span>
          </ElMenuItem>
          <ElMenuItem index="/pictures">
            <span> Pictures </span>
          </ElMenuItem>
          <ElMenuItem index="/videos">
            <span> Videos </span>
          </ElMenuItem>
          <ElMenuItem index="/clipboard">
            <span> Clipboard </span>
          </ElMenuItem>
        </ElMenu>
      </ElAside>
      <ElMain class="content">
        <RouterView />
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>

<style scoped>
.container {
  height: 100%;
}
.main {
  flex: 1 1 content;
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: content;
  height: 100px;
}
.menu {
  height: 100%;
}
.el-main {
  padding: 0;
}
.content {
  flex: 1 1 content;
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: content;
  overflow: hidden;
}
</style>
