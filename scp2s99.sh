#!/bin/bash

source ~/.profile

mvn clean package -U -Dmaven.test.skip=true -Paliyun

#scp config.ini root@192.168.3.99:/data/bidding/
scp target/*.jar root@192.168.3.99:/data/bidding/app.jar