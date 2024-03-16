#!/bin/sh

set -eo pipefail

if [ "${WORKSPACE}" = "" ]; then
    export WORKSPACE=/home/${USER}/workspace/flowershop
    echo "デフォルトのワークスペース:${WORKSPACE}をベースフォルダにします。"
else
    echo "ワークスペース:${WORKSPACE}をベースフォルダにします。"
fi

export ENV_TARGET="devenv"
export COMP_TARGET="devenv"

if [ "$1" = "QA" ]; then
    export ENV_TARGET="qaenv"
    export COMP_TARGET="qaenv"
elif [ "$1" = "QAAPP" ]; then
    export ENV_TARGET="qaenv"
    export COMP_TARGET="qaenv-app"
elif [ "$1" = "QAPY" ]; then
    export ENV_TARGET="qaenv"
    export COMP_TARGET="qaenv-app-python"
elif [ "$1" = "NODE" ]; then
    export COMP_TARGET="devenv-node"
elif [ "$1" = "CY" ]; then
    export COMP_TARGET="devenv-node-with-cypress"
elif [ "$1" = "BLDNODE" ]; then
    export COMP_TARGET="devenv-build-node"
elif [ "$1" = "APP" ]; then
    export COMP_TARGET="devenv-app"
elif [ "$1" = "BLDAPP" ]; then
    export COMP_TARGET="devenv-build-spring"
elif [ "$1" = "PY" ]; then
    export COMP_TARGET="devenv-dev-python"
elif [ "$1" = "PYAPP" ]; then
    export COMP_TARGET="devenv-app-python"
elif [ "$1" = "BLDPY" ]; then
    export COMP_TARGET="devenv-build-python"
fi

# docker-composeで起動するプロジェクト名を指定
export COMPOSE_PROJECT_NAME=flower-pj-${COMP_TARGET}
export COMPOSE_FILE=${WORKSPACE}/docker/${ENV_TARGET}/compose/docker-compose-flowerapps-${COMP_TARGET}.yml
export COMPOSE_HTTP_TIMEOUT=240

echo "PROJECT: ${COMPOSE_PROJECT_NAME} COMPOSE: ${COMPOSE_FILE}"


