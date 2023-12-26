#!/bin/zsh

CUR_DIR=$(cd $(dirname $0); pwd)

cd $CUR_DIR/../

# test if target dir exists
if [ ! -d "target" ]; then
    echo "target dir not exists, please run 'mvn clean package' first"
    exit 1
fi

# test if dnspoddy-1.0.0.jar exists
if [ ! -f "target/dnspoddy-1.0.0.jar" ]; then
    echo "dnspoddy-1.0.0.jar not exists, please run 'mvn clean package' first"
    exit 1
fi

# upload dnspoddy-1.0.0.jar to host 192.168.50.103 with user flora and port 2333 and private key ~/.ssh/id_ed25519
# to directory /home/flora/dnspoddy

scp -P $DEST_HOST_PORT -i $SSH_KEY_PATH target/dnspoddy-1.0.0.jar $DEST_HOST_USER@$DEST_HOST_IP:$DEST_HOST_PATH
