#!/bin/bash

source ~/.profile

mvn clean package -U -Dmaven.test.skip=true -Paliyun

scp target/*.jar root@192.168.3.98:/data/har-crawler-ggzy.jar