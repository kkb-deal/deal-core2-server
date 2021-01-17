#!/usr/bin/env bash
# -*- encoding UTF-8 -*-
# Author: Johny

export PGRDIR=$(cd `dirname $0`; pwd)
export WORKSPACE=${PGRDIR}/../../

source ${PGRDIR}/env.sh

# break shell when fail
set -e

# main base
docker build -t ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE_NAME}:base ${WORKSPACE}

# tester base
docker build -t ${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE_NAME_TESTER}:base -f ${PGRDIR}/testBase.docker ${WORKSPACE}
