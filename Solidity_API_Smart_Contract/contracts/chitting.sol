pragma solidity >=0.4.25 <0.7.0;
pragma experimental ABIEncoderV2;

contract noShowChain
{
// variables
    address payable creator;
    // company
    //users
    struct user {
        string company;
        string pw;
        int point; //i'll buy item ether.... this variable is useless
        int itemCnt;
    }
    user[] userCEO;

    //ledger
    struct Tx {
        string company;
        string pn;
        string name;
        uint reserveTime;
        bool isShow;
        bool TxDone;
    }
    Tx[] ledger;

    //banner items
    struct bannerItem {
        uint term;
        uint price;
    }
    mapping(string=>bannerItem[]) bannerItems; //string is company


    //customer
    //user
    struct customer {
        string id;
        string pw;
        string pn;
        int point;
        int itemCnt;
    }
    customer[] userCustomer;

    //ledger

    //bargain items
    struct giftCardItem {
        string company;
        uint bargainRatio;
    }
    mapping(string=>giftCardItem[]) giftCardItems; //string is user's ID
// methods
    constructor () public {
        creator = msg.sender;
    }
    //company
    //users
    function signInCEO(string memory company, string memory pw) payable public {
        require(msg.value >= 0.001 ether); //signIn fee is 0.001 ether
        //user storage newUser = user(company,pw,0,0);
        userCEO.push(user(company,pw,0,0));
        // bannerItem[] memory item;
        // bannerItems[company] = item;
    }
    function logInCEO(string memory company, string memory pw) public returns(int point, int itemCnt) {
        uint userLength = userCEO.length;
        for(uint i=0; i<userLength; i++){
            if(compareStrings(userCEO[i].company, company) && compareStrings(userCEO[i].pw, pw)){
                return (userCEO[i].point, userCEO[i].itemCnt);
            }
        }
        return(-1,-1);
    }

    //ledger
    function newTx (string memory company, string memory pn, string memory name, uint reserveTime) payable public {
        //Tx memory tempTx = Tx(company,pn,name,reserveTime,false,false);
        ledger.push(Tx(company,pn,name,reserveTime,false,false));
    }
    function TxDone (string memory company, string memory pn, uint timeStamp, bool isShow) payable public {
        // timeStamp is used to check if reserveTime is over
        // if isShow True, call givePoint
        uint TxLength = ledger.length;
        for(uint i=0; i<TxLength; i++){
            if(ledger[i].reserveTime - timeStamp >0 && !ledger[i].TxDone && compareStrings(ledger[i].company, company) && compareStrings(ledger[i].pn, pn)){
                ledger[i].TxDone = true;
                ledger[i].isShow = isShow;
                if(isShow){
                    givePoint(ledger[i].pn);
                }else{
                    penalty(ledger[i].pn);
                }
            }
        }
    }
    function showLedgerByPN(string memory pn) public returns(Tx memory x){ // we have to check pn on client
        uint TxLength = ledger.length;
        for(uint i=TxLength-1; i>=0; i--){
            if(compareStrings(ledger[i].pn, pn)){
                //Tx memory tempTx = ledger[i];
                return ledger[i];
            }
        }
        //Tx memory nullTx = Tx("","","",0,false,false);
        return Tx("","","",0,false,false);
    }
    function givePoint(string memory pn) private { // used in TxDone
        // raise some points of the user PN. if he's not registered this web site, then return
        uint userLength = userCustomer.length;
        for(uint i=0; i<userLength;i++){
            if(compareStrings(userCustomer[i].pn, pn)){
                userCustomer[i].point++;
                break;
            }
        }
    }
    function penalty(string memory pn) private { // used in TxDone
        // call when user no-showed
        uint userLength = userCustomer.length;
        for(uint i=0; i<userLength;i++){
            if(compareStrings(userCustomer[i].pn, pn)){
                userCustomer[i].point = userCustomer[i].point - 10;
                break;
            }
        }
    }

    //banner items
    function showPoint(string memory company) public view returns(int point) {
        uint userLength = userCEO.length;
        for(uint i=0; i<userLength; i++){
            if(compareStrings(userCEO[i].company, company)){
                return (userCEO[i].point);
            }
        }
        return -1;
    }
    function showItems(string memory company) public view returns(int ItemCnt) {// bannerItem[] ,
        int itemCnt = int(bannerItems[company].length);
        return (itemCnt);
    }
    function useItem(string memory company) public payable returns (int ItemCnt){
        if(bannerItems[company].length>0){
            bannerItems[company].pop();
            uint userLength = userCEO.length;
            for(uint i=0; i<userLength; i++){
                if(compareStrings(userCEO[i].company, company)){
                    userCEO[i].itemCnt--;
                    break;
                }
            }
            return int(bannerItems[company].length);
        }else{
            return int(bannerItems[company].length);
        }
    }
    function buyItem(string memory company) public payable returns (int ItemCnt){
        require(msg.value >= 1 ether);
        // bannerItem memory item = bannerItem(30,10);
        bannerItems[company].push(bannerItem(30,10));
        uint userLength = userCEO.length;
        for(uint i=0; i<userLength; i++){
            if(compareStrings(userCEO[i].company, company)){
                userCEO[i].itemCnt++;
                break;
            }
        }
        return int(bannerItems[company].length);
    }

    // customer
    //user
    function signInCustomer(string memory id, string memory pw, string memory pn) public payable {
        require(msg.value >= 0.001 ether); //signIn fee is 0.001 ether
        // customer memory newUser = customer(id,pw,pn,0,0);
        userCustomer.push(customer(id,pw,pn,0,0));
        // giftCardItem[] memory item;
        // giftCardItems[id] = item;
    }
    function logInCustomer(string memory id, string memory pw) public returns(int point, int itemCnt) {
        uint userLength = userCustomer.length;
        for(uint i=0; i<userLength; i++){
            if(compareStrings(userCustomer[i].id, id) && compareStrings(userCustomer[i].pw, pw)){
                return (userCustomer[i].point, userCustomer[i].itemCnt);
            }
        }
        return(-1,-1);
    }
    //ledger
    //bargain Items
    function showPoint_cst(string memory id) public view returns(int point) {
        uint userLength = userCustomer.length;
        for(uint i=0; i<userLength; i++){
            if(compareStrings(userCustomer[i].id, id)){
                return (userCustomer[i].point);
            }
        }
        return -1;
    }
    function showItems_cst(string memory id) public view returns(int ItemCnt) {// bannerItem[] ,
        int itemCnt = int(giftCardItems[id].length);
        return (itemCnt);
    }
    function useItem_cst(string memory id) public payable returns (int ItemCnt){
        if(giftCardItems[id].length>0){
            giftCardItems[id].pop();
            uint userLength = userCustomer.length;
            for(uint i=0; i<userLength; i++){
                if(compareStrings(userCustomer[i].id, id)){
                    userCustomer[i].itemCnt--;
                    break;
                }
            }
            return int(giftCardItems[id].length);
        }else{
            return int(giftCardItems[id].length);
        }
    }
    function buyItem_cst(string memory id) public payable returns (int ItemCnt){
        // giftCardItem memory item = giftCardItem("Cupang",10);
        giftCardItems[id].push(giftCardItem("Cupang",10));
        uint userLength = userCustomer.length;
        for(uint i=0; i<userLength; i++){
            if(compareStrings(userCustomer[i].id, id)){
                userCustomer[i].itemCnt++;
                break;
            }
        }
        return int(giftCardItems[id].length);
    }

      function compareStrings (string memory a, string memory b) public view returns (bool) {
        return (keccak256(abi.encodePacked((a))) == keccak256(abi.encodePacked((b))) );
       }
}
