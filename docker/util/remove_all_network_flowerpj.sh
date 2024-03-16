#!/bin/sh

# flower-pj関連のネットワークを全て削除する
docker network rm `docker network ls -qf name='flower-pj'`
