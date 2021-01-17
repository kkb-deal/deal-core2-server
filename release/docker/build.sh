#!/usr/bin/env bash
# -*- encoding UTF-8 -*-
# Author: Johny

export PGRDIR=$(cd `dirname $0`; pwd)
export WORKSPACE=${PGRDIR}/../../
export VERSION=$1

source ${PGRDIR}/env.sh

# only set VERSION if not set
[ -z "$VERSION" ] && VERSION=latest

# break shell when fail
set -e

# Clean
echo Clean build cache ...
sudo rm -rf ${WORKSPACE}build

# make tool image
echo Step1. make tester image
docker build -t ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE_NAME_TESTER}:${VERSION} -f ${PGRDIR}/tester.docker ${WORKSPACE}

# build server
echo Step2. build target
DOCKER_OPTS="-v $WORKSPACE/build/libs:/workspace/build/libs"
docker run --rm ${DOCKER_OPTS} ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE_NAME_TESTER}:${VERSION} build

# main release image
echo Step3. make release image
docker build -t ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE_NAME}:${VERSION} -f ${PGRDIR}/Dockerfile ${WORKSPACE}
