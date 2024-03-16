#!/bin/sh

# 全てのdocker イメージを削除する
docker rmi `docker images -q`
