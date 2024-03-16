#!/bin/sh

CLR_ON="\033[36;1m"
CLR_OFF="\033[m"

#停止中も含む全てのコンテナを表示
echo -en "${CLR_ON}CREATE DIR & CLEAN docker/qaenv/nginx/ssl -->${CLR_OFF}"
echo ""
mkdir -p ~/workspace/flowershop/docker/qaenv/nginx/ssl
cd ~/workspace/flowershop/docker/qaenv/nginx/ssl
rm -f ./*

# 証明書のキーを生成
# Enter pass phrase for flowershop.local.key: password
# Verifying - Enter pass phrase for flowershop.local.key: password
# Enter pass phrase for flowershop.local.key password
echo -en "${CLR_ON}証明書のキーを生成(2回パスフレーズを入力) -->${CLR_OFF}"
echo ""
openssl genrsa -aes256 -out flowershop.local.key 2048
echo ""

# 証明書のキーからパスワード除去
echo -en "${CLR_ON}証明書パスワード除去(確認パスフレーズ入力)-->${CLR_OFF}"
echo ""
openssl rsa -in flowershop.local.key -out flowershop.local.key
echo ""

# CSR 生成（下記を自動入力する）
# Country Name (2 letter code) []:81
# State or Province Name (full name) []:Tokyo
# Locality Name (eg, city) []:Tokyo
# Organization Name (eg, company) []:none
# Organizational Unit Name (eg, section) []:none
# Common Name (eg, fully qualified host name) []:flowershop.local
# Email Address []:hoge@gmail.com
# A challenge password []:****
echo -en "${CLR_ON}CSR 生成 自動入力   ------------------------>${CLR_OFF}"
echo ""
openssl req -new -key flowershop.local.key -out flowershop.local.csr << EOF
81
Tokyo
Tokyo
none
none
flowershop.local
test@flowershop.local.com
password
none
EOF
echo ""

# マルチドメイン対応SAN(SubjectAlternateName)の追加要素書き出し
echo -en "${CLR_ON}マルチドメイン対応  ------------------------>${CLR_OFF}"
echo ""
echo "subjectAltName = DNS:flowershop.local, DNS:*.flowershop.local" > san.txt

# マルチドメインの追加要素を含むCRT証明書を生成
echo -en "${CLR_ON}CRT証明書生成      ------------------------->${CLR_OFF}"
echo ""
openssl x509 -req -days 3650 -in flowershop.local.csr -signkey flowershop.local.key -out flowershop.local.crt -extfile san.txt
rm -f ./san.txt

echo -en "${CLR_ON}証明書生成完了     ------------------------->${CLR_OFF}"
echo ""
pwd
ls -ltr ./*
