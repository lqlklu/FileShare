<script lang="ts" setup>
import { ref } from "vue";
import { ElScrollbar } from "element-plus";
import { useClipboardStore } from "@/stores/clipboard";
import { Text } from "@/api/clipboard";
import ClipboardItem from "./ClipboardItem.vue";

const clipboard = useClipboardStore();
clipboard.init();

const inp = ref("");
const send = () => {
  if (inp.value !== "") {
    clipboard.sendText(inp.value);
    inp.value = "";
  }
};
</script>

<template>
  <div class="clipboard">
    <div class="content">
      <ElScrollbar>
        <ClipboardItem v-for="it in clipboard.list" :it="it"></ClipboardItem>
      </ElScrollbar>
    </div>
    <div class="send">
      <input type="text" v-model="inp" />
      <button @click="send">Send</button>
    </div>
  </div>
</template>

<style>
.clipboard {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.content {
  flex-grow: 1;
}
.send {
  display: flex;
  margin: 0.3rem 0.4rem;
}
.send input {
  flex-grow: 1;
  margin-right: 0.2rem;
}
</style>
