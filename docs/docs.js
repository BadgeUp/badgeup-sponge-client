'use strict';

const fs = require('fs');
const path = require('path');
const entities = new (require('html-entities').XmlEntities)();
const marked = require('marked');
const renderer = new marked.Renderer();

renderer.code = function(code, lang) {
    code = entities.encode(code);
    // htmlmin:ignore so that whitespace/indentation doesn't get destroyed by the minifier
    return `<!-- htmlmin:ignore --><codeblock type.one-way="'${lang}'">${code}</codeblock><!-- htmlmin:ignore -->`;
}

let pages = [];

const files = ['quickstart', 'events', 'awards', 'persistence'];
for (let fileName of files) {
    const rawData = fs.readFileSync(path.resolve(__dirname, 'source', fileName + '.md'), {
        encoding: 'utf8'
    });
    const tokens = marked.lexer(rawData);

    let pageTitle = '';
    for (let token of tokens) {
        // heading 1
        if (token.type === 'heading' && token.depth === 1) {
            pageTitle = token.text;
        }
    }

    pages.push({
        title: pageTitle,
        id: fileName,
        file: fileName + '.html'
    });

    if (!fs.existsSync(path.resolve(__dirname, 'build'))) {
        fs.mkdirSync(path.resolve(__dirname, 'build'));
    }

    // Add HTML fluff so the docs site can interpret it
    // HACK The require tag is an implementation detail of the docs site, which should not be bleeding over but doesn't seem to work any other way
    let html = 
        `<template>
            <require from="../../codeblock"></require>
            ${marked(rawData, {renderer})}
        </template>`;

    fs.writeFileSync(path.resolve(__dirname, 'build', fileName + '.html'), html);
}

fs.writeFileSync(path.resolve(__dirname, 'build', 'sponge-client-docs.json'), JSON.stringify(pages, null, 4), 'utf-8');
