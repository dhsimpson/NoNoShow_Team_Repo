pragma solidity >=0.4.25 <0.7.0;
pragma experimental ABIEncoderV2;

contract NoNoShow{
  // DB 이외의 파라미터
  address payable creator;
  struct books{
    bytes32 bookID; // 현 예약의 정보들을 토대로 keccak돌린거
    string compID
    string compName; // 매장명
    string keyID;
    string custName; // 고객 이름
    string custPN; // 고객 전화번호
    uint32 date; // 20201414같이
    uint32 time; // 1130같이
    bool isbooked; // 예약됐는지
    bool isShow; // 노쇼면false 쇼면true
  }

  // DB
  struct custDB{
    string keyID; // 회원명+짧은 일련번호
    string phoneNumber;
    string name;
    string ID;
    string password;
    uint32 showCnt;
    uint32 noShowCnt;
    // 토큰, 쿠폰 관련은 나중에 다룸
  }
  struct compDB{//comp 는 company
    string compID;
    string ID;
    string password;
    string addr; // 매장 주소
    string phoneNumber; // 매장 대표번호
    // 멤버십 등록 같은 사항은 아직 미정
  }
  mapping(string=>books[]) LedgerDB;// 모든 예약기록을 담아 두는 핵심 DB
  mapping(string=>books[]) compBooks;// c.f.) 각 업체의 최근 예약 리스트를 갖고 있는 DB
  // 이벤트 함수들 (예약 혹은 예약 확인 시 프론트엔드로 정보를 전달한다.)
  event alertToComp(string keyID,string compID,uint32 date, uint32 time);
  event alertToCust(string keyID, bool ack);
  // 고객
  function custSignIn(string memory name,string memory phoneNumber,string memory id, string memory pw) public returns(bool){

  }
  function custLogIn(string memory id, string memory pw) public view returns(string keyID,bool pass){//

  }
  function book(string memory keyID,string memory compID,uint32 memory date, uint32 memory time)public{
    alertToComp(keyID,compID,date,time);
  }
  function checkBook(string memory keyID) public view returns(books[]){ // 예약됐는지 확인

  }
  // 업체
  function compSignIn(string memory name,string memory phoneNumber,string memory addr, string memory id, string memory pw) public returns(bool){

  }

  function compLogIn() public view returns(bool pass){

  }
  ////업체의 예약 관련 함수
  function bookList(string memory compID,uint32 memory date) public view returns(books[] memory){ // 업체 ID와 날짜로 그날의 예약 리스트 뽑아오기

  }
  function ackBook(string memory bookID, bool memory ack) public{
    alertToCust(bookID,ack);
  }
  function resBook(string memory bookID, bool memory isShow) public{ // 고객이 예약시간에 왔는지 안왔는지 확인

  }
  function checkUser(string memory phoneNumber) public view returns(books[] memory){ // 고객의 최근 노쇼 기록을 확인

  }
  function callBook(string memory keyID,string memory compID,uint32 memory date, uint32 memory time) public returns(bool isbooked) {//전화 예약에 대해 업체가 예약 넣는것

  }
}
