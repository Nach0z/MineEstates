#!/bin/bash
find . -type f -exec git add '{}' \;
git commit -m 'code revision.'
git push -u origin master
