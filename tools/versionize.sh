#!/usr/bin/env bash

set -e

# https://stackoverflow.com/questions/19838064/bash-determine-if-variable-is-empty-and-if-so-exit
[[ -z "$1" ]] && { echo "Parameter 1 - version number - is empty" ; exit 1; }
[[ -z "$2" ]] && { echo "Parameter 2 - version desc - is empty" ; exit 1; }
VERSION=$1
VERSION_DESC=$2
ROOT="/home/mlw/dev/assecor-challenge-mlw"
POM="${ROOT}/implementation/pom.xml"
PYTHON_SCRIPT="tools/set_version_in_file.py"

python3 ${PYTHON_SCRIPT} --pom ${POM} --version ${VERSION}
git add ${POM}
git commit -am "wip lala"
git reset --soft $(git show-ref --tags -d | tail -n 1 | cut -d" " -f1)
git commit -m "new version ${VERSION} - ${VERSION_DESC}"
git tag -a ${VERSION} -m "${VERSION_DESC}"
