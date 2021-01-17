#!/usr/bin/env bash
# -*- encoding UTF-8 -*-
# Author: Johny

PGRDIR=$(cd `dirname $0`; pwd)
WORKSPACE=${PGRDIR}/../../
VERSION=$1

# only set VERSION if not set
[ -z "$VERSION" ] && VERSION=latest

rm -rf ${WORKSPACE}/shared
cp -R ${WORKSPACE}/../shared ${WORKSPACE}/shared