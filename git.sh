#!/bin/bash
find . -path './.git' -prune -o -type f -exec git add '{}' \;
git commit -m 'code revision.'
git push -u origin master
