#!/usr/bin/sh

if [ $# != 1 ]; then
  echo "Usage: bash ${0} UbuntuIPアドレス"
  echo "ホストOSからアクセスするIPを選択ください-->" `hostname -I`
  exit
fi

CLR_ON="\033[36;1m"
CLR_OFF="\033[m"

CERT_DIR=docker-cert
KEY_PW="flowershop@DOCKERpw"
CERT_EXPIREDAYS=36500
# Private鍵ファイル
FILE_PRIVATEKEY=flower.docker.pkey.pem
# CAのファイル名はDockerクライアントの標準ファイル名に合わせる
FILE_CA=ca.pem
# サーバ署名申請ファイル
FILE_CERTSIGNREQ_SERVER=flowershop.docker.local.server.csr
# サーバ鍵と証明書のファイルはDocker Daemonが利用
FILE_SERVERKEY=flower.docker.skey.pem
FILE_SERVERCERT=flower.docker.scert.pem
# クライアント署名申請ファイル
FILE_CERTSIGNREQ_CLIENT=flowershop.docker.local.client.csr
# クライアントの鍵と証明書のファイル名はDockerクライアントの標準ファイル名に合わせる
FILE_CLIENTKEY=key.pem
FILE_CLIENTCERT=cert.pem
# Option指定用一時ファイル
FILE_OPTION=ext.conf

# 証明書情報
# Country Name (2 letter code) [AU]:JP
# State or Province Name (full name) [Some-State]:Tokyo
# Locality Name (eg, city) []:Tokyo
# Organization Name (eg, company) [Internet Widgits Pty Ltd]:none
# Organizational Unit Name (eg, section) []:none
# Common Name (e.g. server FQDN or YOUR name) []:flower.docker.local
# Email Address []:flower@flower.com
CSR_INFO="JP\nTokyo\nTokyo\n.\n.\nflowershop.docker.local\ntest@flowershop.local.com\n"

rm -fr $CERT_DIR
mkdir $CERT_DIR
cd $CERT_DIR

# Private鍵生成
echo -en "${CLR_ON}Private鍵生成 : openssl genrsa -aes256 -passout pass:$KEY_PW -out $FILE_PRIVATEKEY 4096${CLR_OFF}\n"

openssl genrsa -aes256 -passout pass:$KEY_PW -out $FILE_PRIVATEKEY 4096

# 自己CA証明生成
echo -en "${CLR_ON}自己CA証明生成 : openssl req -new -x509 -passin pass:$KEY_PW -days $CERT_EXPIREDAYS -key $FILE_PRIVATEKEY -sha256 -out $FILE_CA ${CLR_OFF}\n"

echo -e $CSR_INFO | openssl req -new -x509 -passin pass:$KEY_PW -days $CERT_EXPIREDAYS -key $FILE_PRIVATEKEY -sha256 -out $FILE_CA

echo ""

# Server証明書署名申請生成
openssl genrsa -out $FILE_SERVERKEY 4096
echo -en "${CLR_ON}Server証明書署名申請生成 : openssl req -subj "/CN=flowershop.docker.local.server" -sha256 -new -key $FILE_SERVERKEY -out $FILE_CERTSIGNREQ_SERVER ${CLR_OFF}\n"

echo -e $CERT_INFO | openssl req -subj "/CN=flowershop.docker.local.server" -sha256 -new -key $FILE_SERVERKEY -out $FILE_CERTSIGNREQ_SERVER

echo ""

# Server証明書生成
echo -en "${CLR_ON}Server証明書生成 : openssl x509 -req -days $CERT_EXPIREDAYS -sha256 -in $FILE_CERTSIGNREQ_SERVER -CA $FILE_CA -CAkey $FILE_PRIVATEKEY -CAcreateserial -passin pass:$KEY_PW -out     $FILE_SERVERCERT -extfile $FILE_OPTION ${CLR_OFF}\n"

echo subjectAltName = IP:${1},IP:127.0.0.1,DNS:localhost > $FILE_OPTION
openssl x509 -req -days $CERT_EXPIREDAYS -sha256 -in $FILE_CERTSIGNREQ_SERVER -CA $FILE_CA -CAkey $FILE_PRIVATEKEY -CAcreateserial -passin pass:$KEY_PW -out $FILE_SERVERCERT -extfile $FILE_OPTION 

echo ""

# Client証明書署名申請生成
echo -en "${CLR_ON}Client証明書署名申請生成 : openssl req -subj '/CN=flowershop.docker.local.client' -new -key $FILE_CLIENTKEY -out $FILE_CERTSIGNREQ_CLIENT ${CLR_OFF}\n"

openssl genrsa -out $FILE_CLIENTKEY 4096 

echo -e $CERT_INFO | openssl req -subj '/CN=flowershop.docker.local.client' -new -key $FILE_CLIENTKEY -out $FILE_CERTSIGNREQ_CLIENT

echo ""

# Client証明書生成
echo -en "${CLR_ON}Client証明書生成 : openssl x509 -req -days $CERT_EXPIREDAYS -sha256 -in $FILE_CERTSIGNREQ_CLIENT -CA $FILE_CA -CAkey  $FILE_PRIVATEKEY -CAcreateserial -passin pass:$KEY_PW -out     $FILE_CLIENTCERT -extfile $FILE_OPTION ${CLR_OFF}\n"

echo extendedKeyUsage = clientAuth > $FILE_OPTION
openssl x509 -req -days $CERT_EXPIREDAYS -sha256 -in $FILE_CERTSIGNREQ_CLIENT -CA $FILE_CA -CAkey  $FILE_PRIVATEKEY -CAcreateserial -passin pass:$KEY_PW -out $FILE_CLIENTCERT -extfile $FILE_OPTION

echo ""

chmod 400 $FILE_PRIVATEKEY $FILE_SERVERKEY $FILE_CLIENTKEY
chmod 444 $FILE_CA $FILE_SERVERCERT $FILE_CLIENTCERT

rm *.csr
rm $FILE_OPTION 

cd ../
ls -ltr $CERT_DIR
