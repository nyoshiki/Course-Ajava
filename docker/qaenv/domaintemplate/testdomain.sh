#!/bin/sh

set -eu

export NEW_DOMAIN=$1

echo "==========================================="
echo "NGINX CONF ---------->"
echo "==========================================="
envsubst '$$NEW_DOMAIN' < nginx_flower.conf.template 
echo "==========================================="
echo "MAKE CERT ---------->"
echo "==========================================="
envsubst '$$NEW_DOMAIN' < makecert.sh.template 

