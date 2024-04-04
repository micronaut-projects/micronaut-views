import React from 'react';
import {hydrateRoot} from 'react-dom/client';

const pageComponentName = __micronaut_boot.rootComponent;

import(`./components/${pageComponentName}.js`).then(module => {
    const PageComponent = module[pageComponentName]
    hydrateRoot(document, <PageComponent {...__micronaut_boot.rootProps}/>)
})
