<script lang="ts" setup>
import { ElUpload, UploadProps as ElUploadProps, UploadProgressEvent, UploadRequestOptions } from "element-plus";
import { UploadAjaxError } from "element-plus/lib/components/upload/src/ajax";

export interface UploadProps {
  action: string;
  method?: string;
  multiple?: boolean;
  drag?: boolean;
  onSuccess?: ElUploadProps["onSuccess"];
  onError?: ElUploadProps["onError"];
  limit?: number;
}
const props = withDefaults(defineProps<UploadProps>(), {
  method: "post",
  multiple: false,
  drag: false,
  onSuccess: () => {},
  onError: () => {},
  limit: 10000,
});

function getError(action: string, option: UploadRequestOptions, xhr: XMLHttpRequest) {
  let msg: string;
  if (xhr.response) {
    msg = `${xhr.response.error || xhr.response}`;
  } else if (xhr.responseText) {
    msg = `${xhr.responseText}`;
  } else {
    msg = `fail to ${option.method} ${action} ${xhr.status}`;
  }
  return new UploadAjaxError(msg, xhr.status, option.method, action);
}

function getBody(xhr: XMLHttpRequest): XMLHttpRequestResponseType {
  const text = xhr.responseText || xhr.response;
  if (!text) {
    return text;
  }
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

const httpRequest: ElUploadProps["httpRequest"] = (options) => {
  const action = options.action;
  const xhr = new XMLHttpRequest();
  xhr.upload.addEventListener("progress", (evt) => {
    const progressEvt = evt as UploadProgressEvent;
    progressEvt.percent = evt.total > 0 ? (evt.loaded / evt.total) * 100 : 0;
    options.onProgress(progressEvt);
  });
  xhr.addEventListener("error", () => {
    options.onError(getError(action, options, xhr));
  });
  xhr.addEventListener("load", () => {
    if (xhr.status < 200 || xhr.status >= 300) {
      return options.onError(getError(action, options, xhr));
    }
    options.onSuccess(getBody(xhr));
  });
  xhr.open(options.method, action, true);
  const form = new FormData();
  form.append(options.file.name, options.file);
  xhr.send(form);
  return xhr;
};
</script>

<template>
  <ElUpload :action="props.action" :method="props.method" :multiple="props.multiple" :drag="props.drag" :on-success="props.onSuccess" :on-error="props.onError" :http-request="httpRequest" :limit="props.limit">
    <slot></slot>
  </ElUpload>
</template>
