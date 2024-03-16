#!/bin/sh

PS3="ログインするコンテナを選択ください(13 終了)"
select CONTAINER_NAME in flower_node_react flower_python_fastapi flower_app_fastapi flower_app_001 flower_db_mysql flower_web_001_qa flower_db_001_qa flower_app_001_qa flower_app_py_001_qa flower_build_java_spring flower_build_python_fastapi quit
do
    echo " select no $REPLY value $CONTAINER_NAME"

    case "$REPLY" in
        13) 
            exit 0
            ;;
        [1-9] | 1[0-2]) 
            break
            ;;
    esac
done

# 指定されたコンテナにログインする
docker exec -it $CONTAINER_NAME /bin/sh
