'use strict';

const fs = require('fs');
const path = require('path');
const marked = require('marked');

let pages = [];

const files = ['quickstart', 'events'];
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

    let html = '<template>\n' + marked(rawData) + '</template>';

    fs.writeFileSync(path.resolve(__dirname, 'build', fileName + '.html'), html);
}

fs.writeFileSync(path.resolve(__dirname, 'build', 'sponge-client-docs.json'), JSON.stringify(pages, null, 4), 'utf-8');
