export function join(...paths: string[]): string {
  const sep = "/";
  const r = new RegExp(sep + "{1,}", "g");
  return paths.join(sep).replace(r, sep);
}

export default {
  join,
};
