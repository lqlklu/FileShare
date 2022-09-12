<script lang="ts" setup>
import { ClipboardContent, Text } from "@/api";

export interface ClipboardItemProps {
  it: ClipboardContent;
}
const props = defineProps<ClipboardItemProps>();
</script>

<style scoped>
.item {
  margin: 0.5rem 1rem;
  border: 1px solid;
  border-radius: 4px;
  user-select: text;
}
.item .wrap {
  padding: 0.5rem 0.6rem;
}
</style>

<template>
  <div class="item">
    <div class="wrap">
      <div class="text" v-if="props.it instanceof Text">
        <div class="content">
          {{ props.it.content }}
        </div>
      </div>
    </div>
  </div>
</template>
