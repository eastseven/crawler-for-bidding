#!/bin/bash

source ~/.profile

mvn clean package -U -Dmaven.test.skip=true -Paliyun

scp target/*.jar root@192.168.3.99:/data/har-crawler-ggzy.jar