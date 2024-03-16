#!/bin/sh

set -eu

export NEW_DOMAIN=$1

envsubst '$$NEW_DOMAIN' < nginx_flower.conf.template > nginx_flower.conf 
envsubst '$$NEW_DOMAIN' < makecert.sh.template > makecert.sh

cp -p nginx_flower.conf ~/workspace/flowershop/docker/qaenv/nginx/config/.
cp -p makecert.sh ~/workspace/flowershop/docker/util/.

