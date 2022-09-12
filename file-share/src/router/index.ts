import { createRouter, createWebHistory, createWebHashHistory } from "vue-router";
import HomeView from "@/components/HomeView.vue";

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/files",
      name: "Files",
      component: () => import("@/components/FilesView.vue"),
      props: (route) => ({
        path: route.query["path"] || "/",
      }),
    },
    {
      path: "/documents",
      name: "Documents",
      component: () => import("@/components/DocumentsView.vue"),
    },
    {
      path: "/downloads",
      name: "Downloads",
      component: () => import("@/components/DownloadsView.vue"),
    },
    {
      path: "/music",
      name: "Music",
      component: () => import("@/components/MusicView.vue"),
    },
    {
      path: "/pictures",
      name: "Pictures",
      component: () => import("@/components/PicturesView.vue"),
    },
    {
      path: "/videos",
      name: "Videos",
      component: () => import("@/components/VideosView.vue"),
    },
    {
      path: "/clipboard",
      name: "Clipboard",
      component: () => import("@/components/ClipboardView.vue"),
    },
  ],
});

export default router;
