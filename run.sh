#!/bin/bash

PROJECT_PATH=/FunMeet_Server/build/libs
PROJECT_NAME=funMeet-0.0.1-SNAPSHOT.jar

CURRENT_PID=$(pgrep -fl $PROJECT_NAME | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
  echo "1-1) 실행중인 프로세스 없음"
else
  echo "1-2) 실행중인 프로세스 종료: $CURRENT_PID"
  kill -9 $CURRENT_PID
  sleep 5
fi

JAR_NAME=$(ls -tr $PROJECT_NAME | tail -n 1)
echo "2-1) 어플리케이션 재실행: $JAR_NAME"

nohup java -jar -Dspring.profiles.active=db $PROJECT_PATH/$JAR_NAME > nohup.out 2>&1 &




