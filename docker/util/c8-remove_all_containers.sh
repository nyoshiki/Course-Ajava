#!/bin/bash

# 全てのコンテナを削除する
docker rm -f `docker ps -aq`

# flower-pj関連のネットワークを全て削除する
docker network rm `docker network ls -qf name='flower-pj'`


