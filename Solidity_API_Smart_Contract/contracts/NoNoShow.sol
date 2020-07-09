pragma solidity >=0.4.25 <0.7.0;
pragma experimental ABIEncoderV2;

contract NoNoShow{
  // DB 이외의 파라미터
  address payable creator;
  struct books{
    bytes32 compID;
    bytes32 keyID;
    string compName; // 매장명
    string custName; // 고객 이름
    string custPN; // 고객 전화번호
    uint32 date; // 20201414같이
    uint32 time; // 1130같이
    uint32 personnel; // 예약인원수
    uint32 bookState; // 0 확인안됨, 1 예약승인, 2 예약거절
    bool isShow; // 노쇼면false 쇼면true
  }

  // DB
  struct custDB{
    bytes32 keyID; // 회원명+짧은 일련번호
    string phoneNumber;
    string name;
    string ID;
    string PW;
    uint32 showCnt;
    uint32 noShowCnt;
    uint32 age;
    // 토큰, 쿠폰 관련은 나중에 다룸
  }
  struct compDB{//comp 는 company
    bytes32 compID;
    uint32 startTime; // 영업시작시간
    string comName;
    string ID;
    string PW;
    string addr; // 매장 주소
    string phoneNumber; // 매장 대표번호
    string img; // 매장 대표 사진
    string description; // 매장 설명
    // 멤버십 등록 같은 사항은 아직 미정
  }
  mapping(string=>custDB) userCust;// 고객 회원 DB
  mapping(string=>compDB) userComp;// 업체 회원 DB
  mapping(bytes32=>books[]) LedgerDB;// 모든 예약기록을 담아 두는 핵심 DB
  mapping(bytes32=>books[]) compBooks;// c.f.) 각 업체의 최근 예약 리스트를 갖고 있는 DB
  mapping(string=>string) userID_finder; // phoneNumber => ID
  books[] resBooks; // 함수에서 예약목록 배열을 반환하기 위해 사용
  // 추가로 필요한 것 : mapping(업체명=>업체코드) , string[] 업체명리스트 변수 와 그와 관련 된 함수들
  // 이벤트 함수들 (예약 혹은 예약 확인 시 프론트엔드로 정보를 전달한다.)

  // 회원가입 관련 이벤트 함수
  event custSignUpEvent(address from, string to, bool pass);
  event compSignUpEvent(address from, string to, bool pass);

  // 예약 관련 이벤트 함수
  event alertToCompEvent(address from, bytes32 keyID,bytes32 compID, uint32 date, uint32 time, uint personnel);
  event alertToCustEvent(address from, bytes32 keyID, bool ack);

  // 조회 관련 이벤트 함수
  event checkBookListEvent(address from, bytes32 compID,books[] bookList);
  event checkNoShowListEvent(address from, string to, books[] noShowList);

  // 생성자
  constructor () public{
    creator = msg.sender; //스마트 컨트랙트 생성 시에 creator 변수에 생성자 지갑 주소를 할당한다.
  }

  // 고객
  function custSignUp(string memory phoneNumber,string memory name,string memory id, string memory pw, uint32 age) public{
    if(bytes(userCust[id].ID).length==0){//회원가입 된 전번인지 확인, 회원이 있을 땐 ID길이가 0이 아님을 이용
      //c.f.) 회원 전번이 바뀌는 경우는 delete userCust[phoneNumber] 하면 될듯
      bytes32 keyID = keccak256( abi.encodePacked( phoneNumber,name,id,pw,age ) );
      userCust[id] = custDB(keyID,phoneNumber,name,id,pw,0,0,age); // keyID는 임의로, 전번+"1234" 로 한다.
      userID_finder[phoneNumber] = id;
      emit custSignUpEvent(creator,phoneNumber,true);
      return;
    }else{
        emit custSignUpEvent(creator,phoneNumber,false);
        return;
    }
  }
  function custLogIn(string memory id, string memory pw) public view returns(string memory,bytes32 keyID,bool pass){//
    if(keccak256(abi.encodePacked((userCust[id].ID)))  == keccak256(abi.encodePacked((id))) && keccak256(abi.encodePacked((userCust[id].PW))) == keccak256(abi.encodePacked((pw)))){
      return ("client",userCust[id].keyID,true);
    }
    return ("","",false);
  }
  function book(bytes32 keyID,bytes32 compID,uint32 date, uint32 time, uint32 personnel)public{
    emit alertToCompEvent(creator,keyID,compID,date,time, personnel); // 예약 내용을 보내주기만 한다. 실제 Tx 생성은 업체가 한다.
  }
  // 업체
  function compSignUp(string memory compName,uint32 startTime, string memory id, string memory pw ,string memory addr,string memory phoneNumber, string memory img, string memory description) public {
    if(bytes(userComp[id].ID).length==0){//회원가입 된 전번인지 확인, 회원이 있을 땐 ID길이가 0이 아님을 이용
      //c.f.) 회원 전번이 바뀌는 경우는 delete userCust[phoneNumber] 하면 될듯
      bytes32 compID = keccak256( abi.encodePacked( phoneNumber,compName,id,pw ) );
      userComp[id] = compDB(compID, startTime, compName, id, pw, addr, phoneNumber, img, description);
      emit compSignUpEvent(creator, phoneNumber, true);
    }else{
      emit compSignUpEvent(creator, phoneNumber, false);
    }
  }
  //function changeCompInfo() => 고객정보 변경 함수가 불필요하게 복잡하기에 추후에 만들기로 한다.

  function compLogIn(string memory id, string memory pw) public view returns(string memory,bytes32 compID, bool pass){ // 원래는 로그인할떄 세션값 넘겨져야하는데..ㅎㅎ
    if(keccak256(abi.encodePacked((userComp[id].ID)))  == keccak256(abi.encodePacked((id))) && keccak256(abi.encodePacked((userComp[id].PW))) == keccak256(abi.encodePacked((pw)))){
      return ("comp",userComp[id].compID,true); // 두 문자열을 비교하기 위해 keccack256 사용이 필수이다.(string 자체로는 주솟값이므로)
    }
    return ("","",false);
  }
  ////업체의 예약 관련 함수
  function bookList(bytes32 compID,uint32 date) public { // 업체 ID와 날짜로 그날의 예약 리스트 뽑아오기
    delete resBooks;
    for(int32 i = int32(compBooks[compID].length-1);i>=0;i--){
        if(compBooks[compID][uint32(i)].date == date){
            resBooks.push(compBooks[compID][uint32(i)]);
        }
    }
    emit checkBookListEvent(creator, compID, resBooks);
  }
  function ackBook(bytes32 compID,bytes32 keyID,string memory compName,string memory custName, string memory custPN, uint32 date,uint32 time,uint32 personnel,uint32 bookState, bool ack) public{
    if(ack){
      LedgerDB[keyID].push(books(compID,keyID,compName,custName,custPN,date,time,personnel,bookState, false));
      compBooks[compID].push(books(compID,keyID,compName,custName,custPN,date,time,personnel,bookState, false));
    }
    emit alertToCustEvent(creator, keyID, ack);
  }
  // 쇼업, 노쇼 결과 처리
  function resBook(bytes32 keyID,bytes32 compID, uint32 date, bool isShow) public{ // 고객이 예약시간에 왔는지 안왔는지 확인
    for(int32 i = int32(LedgerDB[keyID].length - 1);i>=0;i--){
        if(LedgerDB[keyID][uint32(i)].date == date){
            LedgerDB[keyID][uint32(i)].isShow = isShow;
            break;
        }
    }
    for(int32 i = int32(compBooks[compID].length - 1);i>=0;i--){
        if(compBooks[compID][uint32(i)].date == date){
            compBooks[compID][uint32(i)].isShow = isShow;
            break;
        }
    }
  }
  // 승인 대기 예약 상태 바꾸기
  function waitBook(bytes32 keyID,bytes32 compID, uint32 date, uint32 bookState) public{ // 고객이 예약시간에 왔는지 안왔는지 확인
    for(int32 i = int32(LedgerDB[keyID].length - 1);i>=0;i--){
        if(LedgerDB[keyID][uint32(i)].date == date){
            LedgerDB[keyID][uint32(i)].bookState = bookState;
            break;
        }
    }
    for(int32 i = int32(compBooks[compID].length - 1);i>=0;i--){
        if(compBooks[compID][uint32(i)].date == date){
            compBooks[compID][uint32(i)].bookState = bookState;
            break;
        }
    }
  }

  // 전화 예약 승인
  function callBook(bytes32 compID,string memory phoneNumber,string memory compName,string memory custName, string memory custPN, uint32 date,uint32 time, uint32 personnel,uint32 bookState) public {//전화 예약에 대해 업체가 예약 넣는것
    bytes32 keyID = userCust[phoneNumber].keyID;
    LedgerDB[keyID].push(books(compID,keyID,compName,custName,custPN,date,time,personnel,bookState,false));
    compBooks[compID].push(books(compID,keyID,compName,custName,custPN,date,time,personnel,bookState,false));
    emit alertToCustEvent(creator, keyID, true);
  }

  // 업체 회원 공통
  function checkUser(string memory phoneNumber,string memory callerPN ,int32 idx) public { // 고객의 최근 노쇼 기록을 확인
    delete resBooks;
    int32 bookLength = int32(LedgerDB[userCust[userID_finder[phoneNumber]].keyID].length);
    bytes32 keyID = userCust[userID_finder[phoneNumber]].keyID;
    for(int32 i =int32(bookLength - 1);i>bookLength-1-idx;i--){
        if(i<0){break;}
        resBooks.push(LedgerDB[keyID][uint32(i)]);
    }
    emit checkNoShowListEvent(creator,callerPN,resBooks);
  }
}
