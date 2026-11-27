// Card.js
import React from 'react';

// Renders props as plain values: what it shows is what a browser would be given as JSON.
function Card({title, genre, added, pageCount, tags}) {
    return (
        <html>
        <head>
            <title>{title}</title>
            <meta charSet="UTF-8"/>
        </head>
        <body>
            <p>Genre: {genre}</p>
            <p>Added: {added}</p>
            <p>Pages: {pageCount}</p>
            <ul>{tags.map(tag => <li key={tag}>{tag}</li>)}</ul>
        </body>
        </html>
    );
}

export default Card;
