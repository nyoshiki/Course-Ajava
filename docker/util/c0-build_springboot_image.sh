#!/bin/sh

CLR_ON="\033[36;1m"
CLR_OFF="\033[m"

#事前にAPP環境のコンテナは停止・削除する
bash c7-stop_container.sh APP
#事前にQA環境のコンテナは停止・削除する
bash c7-stop_container.sh QAAPP
bash c7-stop_container.sh QA
#自動テストの前提となる開発環境のMySQLコンテナは起動する
bash c1-build_and_start_compose.sh

#停止中も含む全てのコンテナを表示
echo -en "${CLR_ON}BEFORE PRUNE DOCKER CONTAINERS -->${CLR_OFF}\n"
docker ps -a
echo -en "${CLR_ON}BEFORE PRUNE DOCKER IMAGES ------>${CLR_OFF}\n"
docker images

#Dockerは同じタグのイメージがある状態でイメージを再作成すると
#damaged image(none:none)が生成されるためコンテナ未使用のイメージを削除
docker system prune -f

echo -en "${CLR_ON}AFTER PRUNE DOCKER CONTAINERS --->${CLR_OFF}\n"
docker ps -a
echo -en "${CLR_ON}AFTER PRUNE DOCKER IMAGES ------->${CLR_OFF}\n"
docker images

#エラー発生時は中止
set -e

cd ~/workspace/flowershop/code/javaapps/flowershop/

echo -en "${CLR_ON}START GRADLE CLEAN -------------->${CLR_OFF}\n"
echo ""
gradle clean

echo -en "${CLR_ON}START GRADLE BUILD -------------->${CLR_OFF}\n"
echo ""
gradle build

# Flowershopのアプリケーションを実行するために必要な最小限の依存関係を検出
# gradle assemble dockerでイメージ生成時にパラメータに渡すようbuild.graldeで指定
echo -en "${CLR_ON}START JDPS最小JRE構成解析 -------------->${CLR_OFF}\n"
echo ""
cd build/libs && jar xvf flowershop-*-SNAPSHOT.jar
jdeps -R -cp "BOOT-INF/lib/*" --print-module-deps --ignore-missing-deps --multi-release 17  flowershop-0.0.1-SNAPSHOT.jar > flowershop-spring-jre.list

echo -en "${CLR_ON}START GRADLE ASSEMBLE DOCKER ---->${CLR_OFF}\n"
echo ""
cd ~/workspace/flowershop/code/javaapps/flowershop/
gradle assemble docker --info

echo -en "${CLR_ON}BEFORE CLEAN DANGLING IMAGE ----->${CLR_OFF}\n"
docker images

# dangling imageが存在しない場合もエラーで中止としない
set +e

echo -en "${CLR_ON}START CLEAN DANGLING IMAGE ------>${CLR_OFF}\n"
docker rmi $(docker images -f 'dangling=true' -q)

echo -en "${CLR_ON}AFTER  CLEAN DANGLING IMAGE ----->${CLR_OFF}\n"
docker images

echo -en "${CLR_ON}FLOWERSHOP APPS IMAGE CREATED --->${CLR_OFF}\n"
docker images | grep flowershop
