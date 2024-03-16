#!/bin/sh

if [ $# == 1 ]; then
    echo "Usage: bash ${0} \"\"|APP|NODE|QA composeのサービス名"
    echo "${1}のサービス一覧を表示---->"
    bash c5-view_compose_service_list.sh ${1}
    exit
elif [ $# != 2 ]; then
    echo "Usage: bash ${0} \"\"|APP|NODE|QA composeのサービス名"
    echo "composeのサービス名はbash c5-view_compose_service_list.sh \"\"|APP|NODE|QA で確認"
    exit
fi

# docker-composeの定義ファイルの読み込み先定義の取得
source ./c9-set_common_profile.sh ${1}

echo "ENV:${ENV_TARGET}"
echo "PROJECT:${COMPOSE_PROJECT_NAME}"
echo "COMPOSE FILE:${COMPOSE_FILE}"

# docker-composeの定義に従いbashで起動
# docker-compose -p $COMPOSE_PROJECT_NAME run --rm ${2} bash
docker-compose run --rm ${2} bash
