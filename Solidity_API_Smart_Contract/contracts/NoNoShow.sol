pragma solidity >=0.4.25 <0.7.0;
pragma experimental ABIEncoderV2;

contract NoNoShow{
  // DB 이외의 파라미터
  address payable creator;
  struct books{
    string compID;
    string compName; // 매장명
    string keyID;
    string custName; // 고객 이름
    string custPN; // 고객 전화번호
    uint32 date; // 20201414같이
    uint32 time; // 1130같이
    bool isShow; // 노쇼면false 쇼면true
  }

  // DB
  struct custDB{
    string keyID; // 회원명+짧은 일련번호
    string phoneNumber;
    string name;
    string ID;
    string PW;
    uint32 showCnt;
    uint32 noShowCnt;
    // 토큰, 쿠폰 관련은 나중에 다룸
  }
  struct compDB{//comp 는 company
    string compID;
    string comName;
    string ID;
    string PW;
    string addr; // 매장 주소
    string phoneNumber; // 매장 대표번호
    // 멤버십 등록 같은 사항은 아직 미정
  }
  mapping(string=>custDB) userCust;// 고객 회원 DB
  mapping(string=>compDB) userComp;// 업체 회원 DB
  mapping(string=>books[]) LedgerDB;// 모든 예약기록을 담아 두는 핵심 DB
  mapping(string=>books[]) compBooks;// c.f.) 각 업체의 최근 예약 리스트를 갖고 있는 DB
  books[] resBooks; // 함수에서 예약목록 배열을 반환하기 위해 사용
  // 이벤트 함수들 (예약 혹은 예약 확인 시 프론트엔드로 정보를 전달한다.)
  event alertToComp(string keyID,string compID, uint32 date, uint32 time);
  event alertToCust(string keyID, bool ack);
  // 생성자
  constructor () public{
    creator = msg.sender; //스마트 컨트랙트 생성 시에 creator 변수에 생성자 지갑 주소를 할당한다.
  }
  //함수 내에서 사용하기 위한 함수
  function uint2str(uint _i) internal pure returns(string memory){//정수형 4자리 유저코드를 string으로 변환
      if(_i== 0){
          return ("0000");
      }
      uint j = _i;
      uint len;
      while (j != 0) {
          len++;
          j /= 10;
      }
      bytes memory bstr = new bytes(len);
      uint k = len - 1;
      while (_i != 0) {
        bstr[k--] = byte(uint8(48 + _i % 10));
        _i /= 10;
      }
      return (string(bstr));
  }

  // 고객
  function custSignIn(string memory phoneNumber,string memory name,string memory id, string memory pw) public returns(bool){
    if(bytes(userCust[phoneNumber].ID).length!=0){//회원가입 된 전번인지 확인, 회원이 있을 땐 ID길이가 0이 아님을 이용
      //c.f.) 회원 전번이 바뀌는 경우는 delete userCust[phoneNumber] 하면 될듯
      uint userCode = uint( keccak256( abi.encodePacked( phoneNumber,name,id,pw ) ) )%10000;
      string memory str = uint2str(userCode);
      string memory keyID = string(abi.encodePacked(name,str));
      userCust[phoneNumber] = custDB(keyID,phoneNumber,name,id,pw,0,0); // keyID는 임의로, 전번+"1234" 로 한다.
      return true;
    }
    return false;
  }
  function custLogIn(string memory phoneNumber, string memory id, string memory pw) public view returns(string memory keyID,bool pass){//
    if(keccak256(abi.encodePacked((userCust[phoneNumber].ID)))  == keccak256(abi.encodePacked((id))) && keccak256(abi.encodePacked((userCust[phoneNumber].PW))) == keccak256(abi.encodePacked((pw)))){
      return (userCust[phoneNumber].keyID,true);
    }
    return ("",false);
  }
  function book(string memory keyID,string memory compID,uint32 date, uint32 time)public{
    emit alertToComp(keyID,compID,date,time); // 예약 내용을 보내주기만 한다. 실제 Tx 생성은 업체가 한다.
  }
  // 업체
  function compSignIn(string memory compID, string memory compName, string memory id, string memory pw ,string memory addr,string memory phoneNumber) public returns(bool){
    if(bytes(userComp[phoneNumber].ID).length!=0){//회원가입 된 전번인지 확인, 회원이 있을 땐 ID길이가 0이 아님을 이용
      //c.f.) 회원 전번이 바뀌는 경우는 delete userCust[phoneNumber] 하면 될듯
      userComp[phoneNumber] = compDB(compID, compName, id, pw, addr, phoneNumber); // keyID는 임의로, 전번+"1234" 로 한다.
      return true;
    }
    return false;
  }
  //function changeCompInfo() => 고객정보 변경 함수가 불필요하게 복잡하기에 추후에 만들기로 한다.

  function compLogIn(string memory phoneNumber, string memory id, string memory pw) public view returns(bool pass){ // 원래는 로그인할떄 세션값 넘겨져야하는데..ㅎㅎ
    if(keccak256(abi.encodePacked((userComp[phoneNumber].ID)))  == keccak256(abi.encodePacked((id))) && keccak256(abi.encodePacked((userComp[phoneNumber].PW))) == keccak256(abi.encodePacked((pw)))){
      return (true); // 두 문자열을 비교하기 위해 keccack256 사용이 필수이다.(string 자체로는 주솟값이므로)
    }
    return (false);
  }
  ////업체의 예약 관련 함수
  function bookList(string memory compID,uint32 date) public returns(books[] memory){ // 업체 ID와 날짜로 그날의 예약 리스트 뽑아오기
    delete resBooks;
    for(uint32 i = uint32(compBooks[compID].length-1);i>=0;i--){
        if(compBooks[compID][i].date == date){
            resBooks.push(compBooks[compID][i]);
        }
    }
    return resBooks;
  }
  function ackBook(string memory compID,string memory compName,string memory keyID,string memory custName, string memory custPN, uint32 date,uint32 time, bool ack) public{
    LedgerDB[keyID].push(books(compID,compName,keyID,custName,custPN,date,time,false));
    compBooks[compID].push(books(compID,compName,keyID,custName,custPN,date,time,false));
    emit alertToCust(keyID, ack);
  }
  function resBook(string memory keyID,string memory compID, uint32 date, bool isShow) public{ // 고객이 예약시간에 왔는지 안왔는지 확인
    for(uint32 i = uint32(LedgerDB[keyID].length - 1);i>=0;i--){
        if(LedgerDB[keyID][i].date == date){
            LedgerDB[keyID][i].isShow = isShow;
            break;
        }
    }
    for(uint32 i = uint32(compBooks[compID].length - 1);i>=0;i--){
        if(compBooks[compID][i].date == date){
            compBooks[compID][i].isShow = isShow;
            break;
        }
    }
  }
  function checkUser(string memory phoneNumber,uint32 idx) public returns(books[] memory){ // 고객의 최근 노쇼 기록을 확인
    delete resBooks;
    uint32 bookLength = uint32(LedgerDB[userCust[phoneNumber].keyID].length);
    for(uint32 i = bookLength - 1;i>bookLength-1-idx || i>=0;i--){
        resBooks.push(LedgerDB[userCust[phoneNumber].keyID][i]);
    }
    return resBooks;
  }
  function callBook(string memory compID,string memory compName,string memory keyID,string memory custName, string memory custPN, uint32 date,uint32 time) public {//전화 예약에 대해 업체가 예약 넣는것
    LedgerDB[keyID].push(books(compID,compName,keyID,custName,custPN,date,time,false));
    compBooks[compID].push(books(compID,compName,keyID,custName,custPN,date,time,false));
    emit alertToCust(keyID, true);
  }
}
