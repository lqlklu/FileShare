import type { Ref } from "vue";
import { useLocalStorage } from "@vueuse/core";

export const localStorage = <T>(id: string, defaultValue: T): Ref<T> => useLocalStorage(id, defaultValue);
