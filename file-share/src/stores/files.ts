import { defineStore } from "pinia";
import { ElLoading, ElMessage } from "element-plus";

import router from "@/router";
import { lsDirectory, TreeItem, deleteFiles, downloadFiles } from "@/api";
import path from "@/utils/path";
import { localStorage } from "./utils";

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
      path: "",
      pathArr: new Array<PathItem>(),
      rawItems: [] as TreeItem[],
      showHidden: localStorage("show-hidden", false),
      sortMode: "name" as SortMode,
      error: false,
      refreshing: false,
      ok: false,
      selection: new Array<string>(),
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
    selecting(state): boolean {
      return state.selection.length !== 0;
    },
    selected(state): (path: string) => boolean {
      return (path: string) => {
        return state.selection.find((it) => it == path) !== undefined;
      };
    },
  },
  actions: {
    async refresh(): Promise<void> {
      const loading = ElLoading.service({});
      this.refreshing = true;
      try {
        const res = await lsDirectory(this.path);
        this.rawItems = res.items;
        this.error = false;
        this.ok = true;
      } catch (e) {
        this.error = true;
        this.ok = false;
        this.navigate("/");
        throw e;
      } finally {
        loading.close();
        this.refreshing = false;
      }
    },
    async navigate(dest: string): Promise<void> {
      if (dest !== this.path) {
        this.path = dest;
        this.pathArr = splitPath(this.path);
        router.push({ path: "/files", query: { path: dest } });
        await this.refresh();
      }
    },
    sortBy(mode: SortMode) {
      this.sortMode = mode;
    },
    toggleSelect(path: string) {
      if (this.selection.find((it) => it == path) !== undefined) {
        this.selection = this.selection.filter((it) => it != path);
      } else {
        this.selection.push(path);
      }
    },
    clearSelect() {
      this.selection = [];
    },
    async deleteSelected(): Promise<void> {
      await deleteFiles(this.selection);
      this.selection = [];
    },
    async downloadSelected() {
      await downloadFiles(this.selection);
      this.selection = [];
    },
  },
});

interface PathItem {
  name: string;
  dest: string;
}

function splitPath(p: string): Array<PathItem> {
  const ret = new Array<PathItem>();
  ret.push({ name: "sdcard", dest: "/" });
  let t = p.split("/").filter((v) => v != "");
  let s = "";
  t.forEach((v) => {
    s = path.join(s, v);
    ret.push({ name: v, dest: s });
  });
  return ret;
}
