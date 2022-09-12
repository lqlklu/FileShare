import { defineStore } from "pinia";

import { ClipboardContent, connectClipboard } from "@/api/clipboard";

export const useClipboardStore = defineStore("clipboard", {
  state: () => {
    return {
      clipboard: connectClipboard(),
      list: new Array<ClipboardContent>(),
    };
  },
  getters: {},
  actions: {
    init() {
      this.clipboard.onMessage((f) => {
        console.log(f);
        this.list.push(f);
      });
    },
    sendText(content: string) {
      this.clipboard.sendText(content);
    },
  },
});
