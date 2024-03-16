#!/bin/sh

# docker-composeの定義ファイルの読み込み先定義の取得
source ./c9-set_common_profile.sh ${1}

echo "ENV:${ENV_TARGET}"
echo "PROJECT:${COMPOSE_PROJECT_NAME}"
echo "COMPOSE FILE:${COMPOSE_FILE}"

if [ "$1" = "APP" -o "$1" = "PY" -o "$1" = "PYAPP" ]; then
  # 開発環境のMySQLコンテナを起動する
  bash c6-start_container.sh
elif [ "$1" = "QAAPP" -o "$1" = "QAPY" ]; then
  # 検証環境のNginxコンテナMySQLコンテナを起動する
  bash c6-start_container.sh QA
fi

# ビルド済みのコンテナ群をdocker-composeの定義に従い起動
docker-compose -p $COMPOSE_PROJECT_NAME up -d
