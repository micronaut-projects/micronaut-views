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
