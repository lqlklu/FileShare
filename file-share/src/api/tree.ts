import axios from "axios";

import path from "@/utils/path";

// const httpServer = "http://192.168.0.101:8080";
const httpServer = "";

export async function fetchTree(path?: string): Promise<TreeResponse> {
  const pa = `${httpServer}/api/files/tree${path || ""}`.replace(new RegExp("/$"), "");
  const r = await axios.get<TreeResponse>(pa);
  return r.data;
}

export async function navigateFile(p: string) {
  const pa = path.join(`${httpServer}/api/files/raw/`, p);
  window.open(pa, "_blank")?.focus();
}

export interface TreeResponse {
  path: string;
  items: TreeItem[];
}

export interface TreeItem {
  type: string;
  name: string;
  path: string;
  length: number;
  lastModify: number;
}
