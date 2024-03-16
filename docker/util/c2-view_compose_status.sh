#!/bin/sh

# docker-composeの定義ファイルの読み込み先定義の取得
source ./c9-set_common_profile.sh ${1}

echo "ENV:${ENV_TARGET}"
echo "PROJECT:${COMPOSE_PROJECT_NAME}"
echo "COMPOSE FILE:${COMPOSE_FILE}"

# docker-composeで定義されているコンテナの稼働状況を確認
docker-compose -p $COMPOSE_PROJECT_NAME ps
