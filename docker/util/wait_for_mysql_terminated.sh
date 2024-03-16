#!/bin/sh

# docker-composeの定義ファイルの読み込み先定義の取得
source ./c9-set_common_profile.sh 

echo "ENV:${ENV_TARGET}"
echo "PROJECT:${COMPOSE_PROJECT_NAME}"
echo "COMPOSE FILE:${COMPOSE_FILE}"

# MySQLの終了時には完全完了まで待つ
echo "Waiting for MySQL shutdown..."
sh -c 'docker-compose -p ${COMPOSE_PROJECT_NAME} logs -f | { sed "/mysqld: Shutdown complete/ q" && echo "flower_db_mysql shutdown." >&2 && kill $$ ;}' 2>&1 || :
