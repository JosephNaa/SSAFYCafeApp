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
    - Language
        - Kotlin
    - Library
        - Retrofit, Gson, Firebase, Ted, Glide, Kakao API, Naver API, Google API, Coroutine, Lottie, Live Preferences, Jetpack AAC

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
<img width=90% src=https://user-images.githubusercontent.com/17241871/143453674-a4c86510-833f-4468-b205-aa876fe71890.png>

## 주요 기능
- 코드 리팩토링(Coroutine + Retrofit2)
- 스플래시(애니메이션)
- 소셜 로그인(구글, 네이버, 카카오)
- FCM(서버)
- 위젯(알림판)

## 패키지 구조 변경
- 기존 컴포넌트 관통 PJT와 네트워크 관통 PJT 코드를 햡쳐서 정리
- Package 구조를 기능별로 분류    
<img width=30% src=https://user-images.githubusercontent.com/17241871/143452214-58dda7cc-286b-4dea-8b8f-343ee9d35094.png>

## 코드 리팩토링
- Coroutine + Retrofit2 형식으로 리팩토링


## 스플래시 화면
- 애니메이션 효과 적용 
<img width=30% src=https://user-images.githubusercontent.com/17241871/143452607-1df6d5c3-f8a7-4114-8d7f-5c30a45b716e.gif>

## 소셜 로그인
- 구글
- 네이버
- 카카오
<img width=30% src=https://user-images.githubusercontent.com/17241871/143450413-611feb8c-6101-4cd1-bbc5-ee78af06e9d8.jpg>
<img width=30% src=https://user-images.githubusercontent.com/17241871/143450555-73bf173f-fec7-47e9-926d-67fd9d90210c.jpg>
<img width=30% src=https://user-images.githubusercontent.com/17241871/143450621-a04d6e88-e11c-461b-be75-55bebe5c9f9d.jpg>
<img width=30% src=https://user-images.githubusercontent.com/17241871/143450658-dd8ec5e4-375e-4e93-9235-4cd031500554.jpg>

## FCM
- 앱 로그인 시 토큰 값 서버로 전송
- 특정 토큰 또는 브로드캐스트를 통해 앱으로 푸시 알림 전송
- 포그라운드, 백그라운드 수신
<img width=30% src=https://user-images.githubusercontent.com/17241871/143450801-c3567bb5-b25b-444f-95a4-8fff00b7c303.jpg>
<img width=30% src=https://user-images.githubusercontent.com/17241871/143450889-b2c5d421-ff8a-4ed9-a7a0-ac46a97c33c8.jpg>
<img width=30% src=https://user-images.githubusercontent.com/17241871/143450947-c1928ea1-0bec-4dce-856a-7ad2ce65063d.jpg>

## 위젯
- 홈 화면에서 알림을 확인할 수 있는 위젯 기능 개발
- 백그라운드 수신 시 실시간으로 리스트 업데이트
<img width=30% src=https://user-images.githubusercontent.com/17241871/143451062-cd3f0254-00f8-4f98-8788-121f97ba354a.jpg>
