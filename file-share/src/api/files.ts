import axios from "axios";

import path from "@/utils/path";

const httpServer = import.meta.env.PROD === true ? "" : "http://192.168.1.128:8080";
console.log("server", httpServer);

export async function lsDirectory(p: string): Promise<LsResponse> {
  const pa = `${httpServer}/api/files/ls?path=${p}`;
  const r = await axios.get<LsResponse>(pa);
  return r.data;
}

export async function navigateFile(p: string) {
  const pa = `${httpServer}${path.join("/api/files/raw", p)}`;
  window.open(pa, "_blank")?.focus();
}

export interface LsResponse {
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

export async function uploadFiles(base: string, form: FormData) {
  return axios.post(`${httpServer}/api/files?base=${base}`, form, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export async function deleteFiles(files: string[]) {
  return axios.delete(`${httpServer}/api/files`, {
    data: JSON.stringify({
      files,
    } as DeleteFilesRequestBody),
    headers: {
      "Content-Type": "application/json",
    },
  });
}

export interface DeleteFilesRequestBody {
  files: string[];
}

/// paths: ['Download/FileShare/a.txt']
export async function downloadFiles(paths: string[]) {
  paths.forEach((it) => downloadUrl(`${httpServer}/api/files/raw/${it}`, path.basename(it)));
}

function downloadUrl(url: string, name: string) {
  // const a = document.createElement("a");
  // a.href = url;
  // a.download = name;
  // document.body.appendChild(a);
  // a.click();
  // document.body.removeChild(a);
  fetch(url, {
    method: "get",
  })
    .then((res) => {
      return res.blob();
    })
    .then((b) => {
      const a = document.createElement("a");
      a.download = name;
      const href = URL.createObjectURL(b);
      a.href = href;
      a.target = "_blank";
      a.click();
      URL.revokeObjectURL(href);
    });
}
