# 📢 Random Chat Web Site

## 📌 프로젝트 소개

**Random Chat Web Site**는 프론트엔드와 백엔드의 전체적인 개발 흐름을 직접 경험하고자 진행한 실시간 1:1 랜덤 채팅 프로젝트입니다.
Oracle Cloud VM 위에 도커 컨테이너로 프론트엔드와 백엔드를 배포하고, **Nginx Reverse Proxy**를 활용하여 실제 서비스처럼 연동한 구조입니다.

## 🛠️ 사용 기술

### Back-End
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- MySQL
- Redis
- WebSocket
- Java Mail Sender

### Front-End
- React
- Jotai
- React Router
- Axios
- Styled-components
- Socket.io

## 주요 기능
- 회원 가입 (아이디, 비밀번호, 이름, 닉네임, 이메일 인증)
- 로그인 / 로그아웃 (JWT 다중 토큰 발급 및 재발급)
- 회원 정보 수정 (수정 시 자동 로그아웃)
- 아이디 찾기 / 비밀번호 찾기 (이메일 인증 번호 발송)
- 사용자 1:1 랜덤 매칭 (Redis ZSet 기반)
- 실시간 채팅 기능 (WebSocket + Socket.io)
- 자신의 메시지는 좌측, 상대방 메시지는 우측 표시
- 채팅방 나가기 기능

### 매칭 시스템 특징
- Redis를 활용한 대기열 시스템 구현
- 먼저 입장한 사용자에게 매칭 우선권 부여
- 리스트의 중간 요소 삭제에 효율적인 ZSet 자료구조 사용

---

## 📦 배포 구조

- Oracle Cloud VM 인스턴스 기반
- Docker를 통한 프론트엔드 & 백엔드 컨테이너화
- Nginx Reverse Proxy를 통한 서비스 연동

---
