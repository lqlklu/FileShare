export function humanSize(len: number): string {
  let p = len;
  let x = 0;
  const us = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"];
  while (p >= 1024) {
    p /= 1024;
    x += 1;
  }
  return `${p.toFixed(2)}${us[x]}`;
}

console.log(humanSize(1024));
