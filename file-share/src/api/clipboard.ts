const wsServer = import.meta.env.PROD === true ? "/clipboard" : "ws://192.168.1.128:8080/clipboard";

export function connectClipboard(): Clipboard {
  return new Clipboard(new WebSocket(wsServer));
}

export class Clipboard {
  ws: WebSocket;
  q: ((f: ClipboardContent) => void)[];

  constructor(ws: WebSocket) {
    this.ws = ws;
    this.q = [];
    this.ws.onmessage = (m: MessageEvent<string>) => {
      const f = ClipboardContent.decode(m.data);
      this.q.forEach((cb) => {
        cb(f);
      });
    };
  }

  onMessage(cb: (f: ClipboardContent) => void) {
    this.q.push(cb);
  }

  sendText(content: string) {
    this.send(new Text(content));
  }

  send(f: ClipboardContent) {
    this.ws.send(f.encode());
  }
}

export class ClipboardContent {
  type: string;

  constructor(type: string) {
    this.type = type;
  }

  static decode(f: string): ClipboardContent {
    const o = JSON.parse(f);
    if (o.type === "text") {
      return new Text(o.content);
    } else {
      return new Invalid();
    }
  }

  encode(): string {
    return JSON.stringify(this);
  }
}

export class Invalid extends ClipboardContent {
  type: "invalid";

  constructor() {
    super("invalid");
    this.type = "invalid";
  }
}

export class Text extends ClipboardContent {
  type: "text";
  content: string;

  constructor(content: string) {
    super("text");
    this.type = "text";
    this.content = content;
  }
}
