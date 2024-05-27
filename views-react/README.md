# React SSR support for Micronaut

## TODO

1. Eliminate all TODOs from the docs.
2. Make HTTP prefetches run in parallel.
3. Reduce the need for config:
   1. Work out what `micronaut.views.folder` is supposed to be when run from Maven. Get rid of the need to specify this.
   2. Make it configurable and allow the path to the static assets to be configured so it doesn't have to be served from MN itself.
   3. Get rid of the blocking of the event loop when prefetching. Pending answer from MN team about why IO pool switch isn't implemented.
4. Write unit tests.
5. Document what you can and cannot do in GraalJS.
6. Find a way to use `renderToPipeableStream`?
7. Replace `__micronaut_prefetch` with Sam's implementation of fetch() for Micronaut?
8. Document how to do debugging?
9. Implement / get implemented TextEncoder/TextDecoder
10. Update the micronaut-spa-app sample.
