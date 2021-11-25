# 싸피 최종 관통 프로젝트

## 팀 구성
- 나요셉 : js.pekah@gmail.com
- 최현정 : owo399@gmail.com

## 개요
- 삼성청년소프트웨어(SSAFY) 모바일 트랙 1학기 최종 관통 프로젝트에 사용된 코드
- 스프링 + 안드로이드 풀스택으로 모바일 카페 앱 개발

## 기술 스택
- Android
    - IDE
        - Android Studio
    - Minimum SDK Version 21
    - Target SDK Version 30
    - Architecture
        - MVVM
    - Language
        - Kotlin
    - Library
        - Retrofit, Gson, Firebase, Ted, Glide, Kakao API, Naver API, Google API, Coroutine, Lottie, Live Preferences, Live Data, ViewModel

- Spring
    - IDE
        - Spring Tool Suite3
    - maven
    - Language
        - JAVA 8
    - Library
        - mybatis, lombok, swagger, firebase, okhttp3

- Database
    - MySQL

## 일정

- 이미지 넣기

## 주요 기능
- 코드 리팩토링(Coroutine + Retrofit2)
- 스플래시(애니메이션)
- 소셜 로그인(구글, 네이버, 카카오)
- FCM(서버)
- 위젯(알림판)

## 패키지 구조 변경
- 기존 컴포넌트 관통 PJT와 네트워크 관통 PJT 코드를 햡쳐서 정리
- Package 구조를 기능별로 분류

## 코드 리팩토링
- Coroutine + Retrofit2 형식으로 리팩토링

## 스플래시 화면
- 애니메이션 효과 적용 
## 소셜 로그인
- 구글
- 네이버
- 카카오

## FCM
- 앱 로그인 시 토큰 값 서버로 전송
- 특정 토큰 또는 브로드캐스트를 통해 앱으로 푸시 알림 전송
- 포그라운드, 백그라운드 수신

## 위젯
- 홈 화면에서 알림을 확인할 수 있는 위젯 기능 개발
- 백그라운드 수신 시 실시간으로 리스트 업데이트

