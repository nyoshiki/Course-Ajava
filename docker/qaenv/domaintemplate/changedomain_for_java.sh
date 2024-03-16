#!/bin/sh

set -eu

export NEW_DOMAIN=$1

envsubst '$$NEW_DOMAIN' < URLBuilderUtil.java.template > URLBuilderUtil.java

cp -p URLBuilderUtil.java ~/workspace/flowershop/code/javaapps/flowershop/src/main/java/jp/flowershop/controller/common/.

