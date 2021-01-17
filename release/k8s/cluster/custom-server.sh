#!/bin/bash
# Author: MC on the way

PGRDIR=$(cd `dirname $0`; pwd)
WORKDIR=${PGRDIR}/../../../

CHART_DIR=$PGRDIR/../chart
CHART_TMEPLATE_DIR=$CHART_DIR/templates
# get server name
SERVER_NAME=$(basename `cd ${WORKDIR} && pwd`)

# replace appVersion in Chart
sed -i "s/appVersion: .*/appVersion: \"${VERSION}\"/g" $CHART_DIR/Chart.yaml
# replace version in Chart
sed -i "s/version: .*/version: \"${VERSION}\"/g" $CHART_DIR/Chart.yaml
# replace server name in Chart
sed -i "s/template-server/$SERVER_NAME/g" $CHART_DIR/Chart.yaml

# replace server name in Chart/templates
for file in `ls ${CHART_TMEPLATE_DIR}`
do
sed -i "s/template-server/$SERVER_NAME/g" $CHART_TMEPLATE_DIR/$file
done
