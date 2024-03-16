#!/bin/sh

# docker-composeの定義ファイルの読み込み先定義の取得
source ./c9-set_common_profile.sh ${1}

echo "ENV:${ENV_TARGET}"
echo "PROJECT:${COMPOSE_PROJECT_NAME}"
echo "COMPOSE FILE:${COMPOSE_FILE}"

# dockser-composeで起動されたコンテナのログ出力
docker-compose -p ${COMPOSE_PROJECT_NAME} logs
