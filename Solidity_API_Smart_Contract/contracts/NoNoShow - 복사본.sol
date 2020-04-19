pragma solidity >=0.4.25 <0.7.0;
pragma experimental ABIEncoderV2;

contract NoNoShow{
  struct dumdum{
    string dumcom;
    string dumpw;
  }
  struct dums{
    uint cnt;
    dumdum[] darr;
  }
  function dummy() public returns(uint){
    return 10;
  }
  dumdum dd;
  function setDum() public {
    dd.dumcom = "hh";
    dd.dumpw = "ee";
  }
  function dumd() public view returns(dumdum memory){
    return dd;
  }
  mapping(string=>dumdum[]) dmap;
  function setDMap(string memory str) public { // 회원 가입 시기 말고 첫 예약 들어 온 시기에 넣어주면 될
    dmap[str].push(dumdum("1","1"));
  }

  function daRet(string memory a) public view returns(dumdum[] memory){
    return dmap[a];
  }

}
