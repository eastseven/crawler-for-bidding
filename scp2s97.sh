#!/bin/bash

source ~/.profile

mvn clean package -Dmaven.test.skip=true

scp target/*.jar root@192.168.3.97:/root