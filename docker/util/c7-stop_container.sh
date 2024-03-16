#!/bin/sh

# docker-composeの定義ファイルの読み込み先定義の取得
source ./c9-set_common_profile.sh ${1}

echo "ENV:${ENV_TARGET}"
echo "PROJECT:${COMPOSE_PROJECT_NAME}"
echo "COMPOSE FILE:${COMPOSE_FILE}"

if [ "${1}" = "" ]; then
    echo "MySQL コンテナを停止します"
    docker-compose -p ${COMPOSE_PROJECT_NAME} stop
    # MySQLの終了時には完全完了まで待つ
    bash wait_for_mysql_terminated.sh
fi

# docker-composeの定義に従いコンテナ起動しているコンテナを停止後コンテナ定義も削除
# stopだけの場合はコンテナの定義は残る
docker-compose -p ${COMPOSE_PROJECT_NAME} down
if [ $? -ne 0 ]; then
  echo "docker-compose downにてエラーが発生しましたが正常終了します"
  exit 0
fi
