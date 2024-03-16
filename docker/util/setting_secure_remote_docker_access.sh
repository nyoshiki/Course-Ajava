#!/bin/sh

cd docker-cert

if [ ! -d /etc/docker/auth ]; then
  sudo mkdir /etc/docker/auth
fi

sudo cp {ca,flower.docker.scert,flower.docker.skey}.pem /etc/docker/auth/.

if [ -f /etc/docker/daemon.json ]; then
  sudo rm /etc/docker/daemon.json
fi

sudo tee /etc/docker/daemon.json << EOF
{
  "debug": true,
  "tls": true,
  "tlsverify": true,
  "tlscacert": "/etc/docker/auth/ca.pem",
  "tlscert": "/etc/docker/auth/flower.docker.scert.pem",
  "tlskey": "/etc/docker/auth/flower.docker.skey.pem",
  "hosts": [
    "unix:///var/run/docker.sock", 
    "tcp://0.0.0.0:2376"
  ],
  "features": { "buildkit": true }
}
EOF

if [ -f /etc/systemd/system/docker.service.d/override.conf ]; then
  sudo rm /etc/systemd/system/docker.service.d/override.conf
fi

if [ ! -d /etc/systemd/system/docker.service.d ]; then
  sudo mkdir /etc/systemd/system/docker.service.d
fi

sudo tee /etc/systemd/system/docker.service.d/override.conf << EOF
[Service]
ExecStart=
ExecStart=/usr/bin/dockerd
#ExecStart=/usr/bin/dockerd --tlsverify --tlscacert=/etc/docker/auth/ca.pem --tlscert=/etc/docker/auth/flower.docker.scert.pem --tlskey=/etc/docker/auth/flower.docker.skey.pem -H tcp://0.0.0.0:2376 -H fd:// --containerd=/run/containerd/containerd.sock
EOF

sudo systemctl daemon-reload
sudo systemctl restart docker

sudo apt-get install net-tools

sudo netstat -lntp | grep dockerd
