#!/bin/bash
find . -path './.git' -prune -o -type f -exec git add '{}' \;
if [ -z "$@" ]
then
	git commit -m 'Generic code revision'
else
	git commit -m "$@"
fi

git push -u origin master
