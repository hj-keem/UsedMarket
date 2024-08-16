# 🛍️ UsedMarket 🛒
Security와 JWT를 적용한 CRUD 기능 중고마켓 프로젝트

## 📂   프로젝트 소개
- 해당 프로젝트는 `중고마켓` 이라는 주제로 `CRUD` 기능에 `Spring Security`를 적용한 프로젝트   
- 기존 프로젝트에서 writer, password를 사용하여 CRUD 권한을 구현했었지만 해당 프로젝트에서는 `Jwt 토큰` 값으로 대체    
- `Role`과 `Authority`간의 `ORM 설정`으로 권한에 따른 역할 지정 및 `USER 생성`(회원가입)    

-------------

## 🗒️  테스트 방법
<details>
<summary> Postman 준비 </summary>

- Postman 설치 [Postman API Platform](https://www.postman.com/downloads/)     
- 해당 레포지토리 `Clone` & 첨부된 `POSTMAN COLLECTION` 파일을 자신의 Postman에 `import`

</details>
<details>
<summary> DB 준비</summary>

- `mysql`을 사용했기 때문에 yml 파일을 참고하여 `DataBase`연결   
  (저는 IntelliJ 커뮤니티 버전 DB Navigator Plugin을 사용했습니다.)
  <img width="708" alt="스크린샷 2024-08-16 오후 11 15 34" src="https://github.com/user-attachments/assets/8c345acf-32f3-47a7-bd84-5cfce2965c5a">

</details>
<Details>
<summary> Jwt 준비 </summary>

- 사진과 같이 Postman의 `Authorization` -> `Bearer Token` -> `Token 입력`   
  (GET 요청을 제외한 모든 요청에 위와 같이 토큰 삽입)
  <img width="684" alt="스크린샷 2024-08-17 오전 2 45 37" src="https://github.com/user-attachments/assets/01f8adc5-db0b-4372-b16b-8c5f99fb1547">
</Details>

-------------

## 📓 각 API 및 반환값
| 판매 게시글 작성 | EndPoint               |
|-----------|------------------------|
| 게시글 생성    | POST /items            |
| 게시글 단독 조회 | GET /items/{itemId}    |
| 게시글 전부 조회 | GET /items             |
| 게시글 수정    | PUT /items/{itemId}    |
| 이미지 추가    | PUT /items/{itemId}    |
| 게시글 삭제    | DELETE /items/{itemId} |

| 댓글       | EndPoint                                   |
|----------|--------------------------------------------|
| 댓글 작성    | POST /items/{itemId}/reply                 |
| 댓글 단독 조회 | GET /items/{itemId}/reply/{replyId}        |
| 댓글 전부 조회 | GET /items/{itemId}/reply                  |
| 댓글 수정    | PUT /items/{itemId}/reply/{replyId}        |
| 댓글 삭제    | Delete /items/{itemId}/reply/{replyId} |

| 네고 제안      | EndPoint                                   |
|------------|--------------------------------------------|
| 제안 작성      | POST /items/{itemId}/proposals                 |
| 제안 조회(All) | GET /items/{itemId}/proposals                |
| 제안 수정      | PUT /items/{itemId}/proposals/{negoId}        |
| 제안 삭제      | DELETE /items/{itemId}/proposals/{negoId} |
| 수락/거절 상태 수정 | PUT /items/{itemId}/proposals/{negoId}/status  |
| 확정 상태 수정    | PUT /items/{itemId}/proposals/{negoId}/confirm |

| 회원가입 & jwt | EndPoint              |
|------------|-----------------------|
| 유저 생성      | POST /users/register        |
| Jwt 발급     | POST /token/issue  |
| Jwt 검증     | GET /token/secured |


-------------

##  🏁 프로젝트 개발 과정


### ⏲️ 개발 기간
- [CURD] 2024.03 (약 6일)
- [Security 적용] 2024.08.15 ~ 16 (2일)


### ⚙️  개발 환경
- Java 17
- JDK GraalVM CE 17
- Gradle



### 🔨️  개발 도구
#### [BackEnd]   
- Language : java
- Framework : Spring boot
- IDE : IntelliJ IDEA CE
- DataBase : MySQL
- API Platform : Postman
- Source Control : Git

-------------

## 📍 주요 기능
### 1. 요구사항
<Details>
<summary> 중고 물품 등록 </summary>

- 물품 정보 생성 ( Jwt을 이용하여 유저 검증 필요 )
- 물품 정보 조회 
- 물품 정보 수정 ( Jwt을 이용하여 유저 검증 필요 )
- 물품 정보 삭제 ( Jwt을 이용하여 유저 검증 필요 )
</Details>


<Details>
<summary> 댓글 </summary>

- 물품에 대한 댓글 달기 ( Jwt을 이용하여 유저 검증 필요 )
- 물품에 대한 댓글 조회 
- 물품에 대한 댓글 수정 ( Jwt을 이용하여 유저 검증 필요 )
- 물품에 대한 댓글 삭제 ( Jwt을 이용하여 유저 검증 필요 )
- 댓글에 대한 대댓글 달기 ( Jwt을 이용하여 유저 검증 필요 )
</Details>


<Details>
<summary> 구매 제안 </summary>

- 등록된 물품에 대하여 구매 제안 및 가격 제시 ( Jwt을 이용하여 유저 검증 필요 )
- 등록된 물품에 대하여 제안 조회 ( 읽기는 모두 허용 )
- 등록된 제안 수정 ( Jwt을 이용하여 유저 검증 필요 )
- 등록된 제안 삭제 ( Jwt을 이용하여 유저 검증 필요 )
- 등록된 제안 수락/거절 ( Jwt을 이용하여 `물품 등록자` 검증 후 수락으로 상태 값 변경 )
- 등록된 제안 확정 (Jwt을 이용하여 `제안 등록자` 검증 후 수락으로 상태 값 변경 )
- 구매 제안이 확정될 경우 판매 물품의 상태는 `판매 완료`로 변경
- 구매 제안이 확정될 경우 다른 구매 제안들의 상태는 모두 `거절`로 변경
</Details>

-------------
## 🎤 시스템 구현

### [ 회원가입 및 Jwt 발급 / 검증]
<Details>
<summary>Postman 결과 확인하기</summary>

1. 회원가입   
   `username`과 `password`를 입력하여 유저 생성   
   (`password-check`와 `password` 불일치 시 실패메세지 표시)
   <img width="600" alt="회원가입" src="https://github.com/user-attachments/assets/8bc2abb3-dc5f-4416-96b5-a50371a12802">
   

2. Jwt 토큰 발급 및 검증   
   회원가입 때 사용한 `username`과 `password`를 이용해 `Jwt` 발급   
   발급 받은 `Token`을 `Bearer Token`에 입력하여 검증, 정상적인 토큰인지 확인
   <img width="600" alt="토큰발급" src="https://github.com/user-attachments/assets/0f95f26b-4e70-45c7-982e-1876289085ca">
   <img width="600" alt="검증" src="https://github.com/user-attachments/assets/01a86179-f1d3-4b7d-80e1-4109e8a8515f">

</Details>

### [ 중고 물품 게시글 CRUD ]
<Details>
<summary>Postman 결과 확인하기</summary>

1. 게시글 생성     
   `@RequestPart`를 통해 `json 객체(dto)`와 `MultipartFile`을 동시 전송    
   <img width="600" alt="게시글 생성" src="https://github.com/user-attachments/assets/d7b165f0-46b7-416d-96ef-a1dee01f0db5">


2. 게시글 단독조회 & 전체조회   
   `Pagenation`을 이용한 전체 댓글 조회
   <img width="600" alt="단독조회" src="https://github.com/user-attachments/assets/80329e23-557e-4b1b-9ff4-aa5942229fac">
   <img width="600" alt="전체조회" src="https://github.com/user-attachments/assets/978ed940-748e-48ab-9049-bb432f503e3c">


3. 수정 및 수정결과   
   <img width="600" alt="수정" src="https://github.com/user-attachments/assets/456b5cf2-36f0-4299-a4ec-5d60d999cce9">
   <img width="600" alt="수정결과" src="https://github.com/user-attachments/assets/87d42491-33ba-4593-b389-f4cfcf5cc2d8">


4. 삭제 및 삭제결과     
   <img width="600" alt="삭제하기" src="https://github.com/user-attachments/assets/6c89363f-259b-4cc9-8c1a-757c6772a06e">
   <img width="600" alt="삭제결과" src="https://github.com/user-attachments/assets/5943f74c-1a55-4bfd-85bb-80fbc9f98118">

</Details>

### [ 댓글 및 제안 CRUD ]
<Details>
<summary>Postman 결과 확인하기 (댓글)</summary>

1. 댓글 생성     
   `itemId`를 통해 게시물에 속한 댓글임을 확인     
   <img width="600" alt="댓글생성" src="https://github.com/user-attachments/assets/9e710c57-4cae-45de-9659-11b3bbc786dc">


2. 댓글 전체 조회      
   `Pagenation`을 이용한 전체 댓글 조회     
   <img width="600" alt="댓글전체조회" src="https://github.com/user-attachments/assets/2cbff725-9a28-4a16-a2a8-93ee0e58c61d">


3. 댓글 수정 & 결과     
   <img width="600" alt="댓글수정" src="https://github.com/user-attachments/assets/2d653937-dcd4-49ca-9c8d-db14a72b2bbd">
   <img width="600" alt="수정결과" src="https://github.com/user-attachments/assets/1fc1be50-ef19-45ef-aa2f-6ea6c46b6957">


4. 댓글 삭제 & 결과
   <img width="600" alt="삭제" src="https://github.com/user-attachments/assets/91164383-8a20-4ce7-9819-9f22c146e0e4">
   <img width="600" alt="삭제결과" src="https://github.com/user-attachments/assets/8572914e-2ee6-45f1-8a6f-cff8645f5534">

</Details>
<Details>
<summary>Postman 결과 확인하기 (제안)</summary>

1. 제안 생성 및 조회       
   `itemId`를 통해 해당 게시물에 속한 것을 표시, `Pagenation`을 이용한 전체 댓글 조회           
   <img width="600" alt="네고생성" src="https://github.com/user-attachments/assets/486f4f9f-e9cf-4238-b6cd-0ed28d10c233">
   <img width="600" alt="네고생성3개 조회" src="https://github.com/user-attachments/assets/4554e0fe-0483-45f6-8d1d-dc50375f7fef">


2. 제안 수락         
   물품 등록자가 `Status`를 `수락` 혹은 `거절` 상태로 변경      
   이 때 `Bearer Token`은 `물품등록자`의 `Jwt` 값을 입력 ( 다른 사용자의 토큰 입력 시 403 Forbidden )
   <img width="600" alt="물품등록자가 수락" src="https://github.com/user-attachments/assets/46023c76-320e-46d1-8c3f-fc8354430b9c">


4. 제안 확정     
   Status가 `수락` 상태일 때 제안자는 `확정`으로 변경         
   이 때 `Bearer Token`은 `제안자`의 `Jwt` 값을 입력 ( 다른 사용자의 토큰 입력 시 403 Forbidden )   
   <img width="600" alt="제안자가 확정" src="https://github.com/user-attachments/assets/daedd6dd-544c-40d4-9dc1-75863ad1a1bb">


5. 제안 거절   
  `Status`가 `확정` 상태로 변경될 시 다른 제안들의 `Status`는 `거절`로 변경    
   <img width="600" alt="확정시 다른거 거절" src="https://github.com/user-attachments/assets/375380df-7f92-4225-b0d3-3eb838d931fc">

</Details>










