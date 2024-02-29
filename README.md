# Spring Boot Up & Running

## 프로젝트 소개
- 도서 <Spring Boot Up & Running> 를 읽고 스터디 목적의 프로젝트임

## 데이터베이스
- 인메모리 DB 인 `H2 DB` 를 사용함
- $H2_HOME/bin/h2.sh 수행하여 콘솔 실행
  - 참고) https://bcp0109.tistory.com/315
  - 나의 경우 `~/study/h2` 경로가 $H2_HOME 임.

## HTTP 기반 CLI 테스트 툴
- HTTPie 활용
- 프로젝트 Run > Terminal
- 하기 명령어 수행
  - $ http :8080/URI
  - ex) $ http :8080/coffees

## springboot actuator
- HTTPie 활용해서 actuator/env 값 조회 시, value가 "*****" 와 같이 노출되는 경우가 있음
  - 예) $ http :8080/actuator/env
```
    "greeting.name": {
        "origin": "class path resource [application.properties] - 1:15",
        "value": "******"
    },
```
- 이는 민감정보이기 대문에 hidden 처리된 것이며, 노출시키기 위해서는 application.properties 에 아래와 같이 설정 필요
```
management.endpoint.env.show-values=always
``` 
  - 결과
```
    "greeting.name": {
        "origin": "class path resource [application.properties] - 1:15",
        "value": "Dakota"
    },
```
- 참고) https://semtul79.tistory.com/16
