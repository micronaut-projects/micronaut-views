// Host globals that a JavaScript engine embedded in a JVM does not provide, but that React's
// server renderer reaches for. Evaluated before the user's server bundle, because React captures
// these at module-evaluation time: install them afterwards and it has already taken its fallback
// path, or failed.
//
// MessageChannel is what React 19's scheduler uses to yield between units of work. React 18 fell
// back to a timer when it was absent, which is why React 18 server-renders on GraalJS today and
// React 19 fails outright with "ReferenceError: MessageChannel is not defined" -- a 500 per
// render rather than a slow page.
//
// Delivery is a microtask rather than a timer on purpose: an engine without MessageChannel
// generally has no setTimeout either, so a timer-based shim swaps one missing global for another.
if (typeof globalThis.MessageChannel === 'undefined') {
  globalThis.MessageChannel = class MessageChannel {
    constructor() {
      const port1 = {onmessage: null};
      const port2 = {
        postMessage(data) {
          Promise.resolve().then(() => {
            if (typeof port1.onmessage === 'function') {
              port1.onmessage({data});
            }
          });
        },
        close() {
          port1.onmessage = null;
        }
      };
      port1.postMessage = port2.postMessage;
      port1.close = port2.close;
      this.port1 = port1;
      this.port2 = port2;
    }
  };
}

// URL and URLSearchParams are what React Router reaches for while server rendering, and an engine
// embedded in a JVM does not provide them either. Without them every server-rendered route that
// touches the router returns 500, so every project doing React SSR on GraalJS has been carrying its
// own copy -- pyronaut-petclinic and pyronaut-full-stack-template both did.
//
// This is a WHATWG-compatible subset, not a specification-complete implementation: enough for
// routing and for reading query parameters. A real engine global always wins, so this costs nothing
// where the platform has one.
if (typeof globalThis.URLSearchParams === 'undefined') {
  globalThis.URLSearchParams = class URLSearchParams {
    constructor(value = '') {
      this.values = new Map();
      const query = String(value).replace(/^\?/, '');
      if (query) {
        for (const pair of query.split('&')) {
          const [key, val = ''] = pair.split('=');
          this.append(decodeURIComponent(key), decodeURIComponent(val));
        }
      }
    }

    append(key, value) {
      const values = this.values.get(String(key)) || [];
      values.push(String(value));
      this.values.set(String(key), values);
    }

    get(key) {
      const values = this.values.get(String(key));
      return values && values.length ? values[0] : null;
    }

    getAll(key) {
      return [...(this.values.get(String(key)) || [])];
    }

    has(key) {
      return this.values.has(String(key));
    }

    delete(key) {
      this.values.delete(String(key));
    }

    toString() {
      return [...this.values]
        .flatMap(([key, values]) =>
          values.map((value) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        )
        .join('&');
    }
  };
}

if (typeof globalThis.URL === 'undefined') {
  globalThis.URL = class URL {
    constructor(value, base = 'http://localhost') {
      let text = String(value);
      if (!/^[a-z][a-z0-9+.-]*:\/\//i.test(text)) {
        text = `${String(base).replace(/\/$/, '')}/${text.replace(/^\//, '')}`;
      }
      const match = text.match(/^([^:]+:\/\/[^/]+)(\/[^?#]*)?(\?[^#]*)?(#.*)?$/);
      this.origin = match ? match[1] : '';
      this.pathname = (match && match[2]) || '/';
      this.search = (match && match[3]) || '';
      this.hash = (match && match[4]) || '';
      this.href = `${this.origin}${this.pathname}${this.search}${this.hash}`;
      this.searchParams = new globalThis.URLSearchParams(this.search);
    }
  };
}
