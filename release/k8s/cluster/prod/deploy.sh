#!/usr/bin/env bash
# -*- encoding UTF-8 -*-
# Author: Johny
set -e

export PGRDIR=$(cd `dirname $0`; pwd)
export WORKDIR=${PGRDIR}/../../../../
export VERSION=$1

source ${WORKDIR}/release/docker/env.sh

# only set VERSION if not set
[ -z "$VERSION" ] && VERSION=latest

# sh custom-server script
sh ${PGRDIR}/../custom-server.sh

# Different environment release names cannot be duplicated in different namespaces of the same cluster
export RELEASE_ENV=$(basename ${PGRDIR})
export RELEASE_NAME=${DOCKER_INSTANCE_NAME}
export NAMESPACE=kuick-prod

export HELM_OPTS=" \
    --namespace ${NAMESPACE} \
    -f ${PGRDIR}/values.yaml \
    --set armsPilotCreateAppName=${DOCKER_INSTANCE_NAME}-${RELEASE_ENV} \
    --set namespace=${NAMESPACE} \
    --set image.repository=${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE_NAME} \
    --set image.tag=${VERSION} \
    --set environment.SENTRY_SERVERNAME=${DOCKER_IMAGE_NAME} \
    --set environment.aliyun_logs_${RELEASE_NAME}-stdout=stdout \
    --set environment.aliyun_logs_${RELEASE_NAME}_ttl=15 \
    --timeout 1800s \
    --wait
"

helm upgrade --install --force --debug --version ${VERSION} ${HELM_OPTS} ${RELEASE_NAME}  ${WORKDIR}/release/k8s/chart/

echo Flags: "--debug:simulate an upgrade   --dry-run:enable verbose output --wait:sync status deploy"