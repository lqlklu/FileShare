import type { Ref } from "vue";
import { defineStore } from "pinia";
import { ElLoading, ElMessage } from "element-plus";
import { useLocalStorage } from "@vueuse/core";

import { fetchTree, TreeItem } from "@/api/tree";
import path from "@/utils/path";

const ls = <T>(id: string, defaultValue: T): Ref<T> => useLocalStorage(id, defaultValue);

export type SortMode = "name" | "nameReverse" | "time" | "timeReverse";

export type SortFn = (a: TreeItem, b: TreeItem) => number;

const nameSortFn: SortFn = (a: TreeItem, b: TreeItem) => {
  if (a.type == "dir" && b.type != "dir") {
    return -1;
  } else if (a.type != "dir" && b.type == "dir") {
    return 1;
  } else {
    if (a.name == b.name) {
      return 0;
    } else if (a.name.toLowerCase() > b.name.toLowerCase()) {
      return 1;
    } else {
      return -1;
    }
  }
};

export const useFilesStore = defineStore("files", {
  state: () => {
    return {
      path: ls("path", ""),
      pathArr: new Array<{ name: string; dest: string }>(),
      rawItems: [] as TreeItem[],
      showHidden: ls("show-hidden", false),
      sortMode: "name" as SortMode,
      error: false,
      fetching: false,
      ok: false,
    };
  },
  getters: {
    items(state): Array<TreeItem> {
      if (!this.showHidden) {
        return state.rawItems.filter((v) => !v.name.startsWith(".")).sort(this.sortFn);
      } else {
        return state.rawItems.sort(this.sortFn);
      }
    },
    sortFn(state): SortFn {
      if (state.sortMode == "name") {
        return nameSortFn;
      } else {
        return nameSortFn;
      }
    },
  },
  actions: {
    async fetch(dest?: string): Promise<void> {
      const loading = ElLoading.service({});
      this.fetching = true;
      try {
        if (!dest) {
          dest = this.path;
        }
        const r = await fetchTree(dest);
        this.path = r.path;
        this.rawItems = r.items;
        let t = r.path.split("/").filter((v) => v != "");
        let s = "";
        let tar = [{ name: "sdcard", dest: "/" }];
        t.forEach((v) => {
          s = path.join(s, v);
          tar.push({ name: v, dest: s });
        });
        this.pathArr = tar;
        this.error = false;
        this.ok = true;
      } catch (e) {
        ElMessage.error("Loading error");
        this.error = true;
        this.ok = false;
        throw e;
      } finally {
        loading.close();
        this.fetching = false;
      }
    },
    sortBy(mode: SortMode) {
      this.sortMode = mode;
    },
  },
});
