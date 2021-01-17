#!/usr/bin/env bash
# -*- encoding UTF-8 -*-
# Author: Johny

# Sync
echo "Will sync local and remote branch refs"
git fetch --prune origin

# Env, it must after git fetch --prune origin
BRANCH_LIST=`git branch -r --merged develop| grep -v develop|grep -v master|grep -v HEAD| cut -d/ -f2-`

if [ -n "$BRANCH_LIST" ];
then
    # list branch
    echo "The follow branch will be deleted!"
    echo ${BRANCH_LIST}
    # Clean branch
    echo "Will clean merged remote branch!"
    git branch -r --merged develop| grep -v develop|grep -v master|grep -v HEAD| cut -d/ -f2- | xargs -n 1 git push --delete origin
else
    echo "Not found remote merged branch need clean!!!"
fi