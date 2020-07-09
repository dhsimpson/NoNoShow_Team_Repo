// configuration web3
const Web3 = require('web3');
const ABI = require('./NoNoShow.json');
const Tx = require('ethereumjs-tx').Transaction;
const abi = ABI.abi;
const config = require('./config.json');
const wallet = config.walletAddress
const contractAddress = config.contractAddress
const url = config.url
const web3 = new Web3(new Web3.providers.WebsocketProvider(url));
const test_contract = new web3.eth.Contract(abi,contractAddress);
const privatekey = config.privateKey
const privateKey = Buffer.from(privatekey,"hex");

// 회원 관리

// 고객 회원가입 및 로그인
module.exports.custSignUp = async function(wallet,PN,name,id,pw,birth){
  try{
    const block = await web3.eth.getBlockNumber();
    var txCount = await web3.eth.getTransactionCount(wallet,block);
    console.log(txCount);
    const txData = {
      nonce: txCount,//'0x10'
      gasLimit: web3.utils.toHex(3000000),
      gasPrice: web3.utils.toHex(3000000),
      to: contractAddress,
      from: wallet,
      data: test_contract.methods.custSignUp(PN,name,id,pw,birth).encodeABI()
    };
    var tx = new Tx(txData,{'chain':'ropsten'});
    tx.sign(privateKey);
    var serialTx = tx.serialize();
    var res = await web3.eth.sendSignedTransaction(`0x${serialTx.toString('hex')}`);
  }catch(e){
    console.log(e);
  }
 // await process.exit(0);
}

module.exports.subCustSignUp = async function(res){
 try{
   const block = await web3.eth.getBlockNumber();
   test_contract.events.custSignUpEvent({fromBlock:block, toBlock: 'latest'},async function(err,event){
     if(err){console.log(err);}
     else{ console.log(event.returnValues);
       if(event.returnValues[2]){res.send(event.returnValues);}
       else{res.send("이미 있는 아이디 입니다.");}
     }
   });
 }catch(e){
   console.log(e);
 }
}

module.exports.custLogIn = async function(id,pw){
  try{
     const result = await test_contract.methods.custLogIn(id,pw).call(); //"madcow","iamblackcow"
     return result;
  }catch(e){
    console.log(e);
  }
}
// 업체 회원가입 및 로그인
module.exports.compSignUp = async function(wallet, name, startTime, id, pw, address, phoneNumber, img, description){
  try{
    const block = await web3.eth.getBlockNumber();
    var txCount = await web3.eth.getTransactionCount(wallet,block);
    console.log(txCount);
    const txData = {
      nonce: txCount,//'0x10'
      gasLimit: web3.utils.toHex(3000000),
      gasPrice: web3.utils.toHex(3000000),
      to: contractAddress,
      from: wallet,
      data: test_contract.methods.compSignUp(name, startTime, id, pw, address, phoneNumber, img, description).encodeABI()
    };
    var tx = new Tx(txData,{'chain':'ropsten'});
    tx.sign(privateKey);
    var serialTx = tx.serialize();
    var res = await web3.eth.sendSignedTransaction(`0x${serialTx.toString('hex')}`);
  }catch(e){
    console.log(e);
  }
 // await process.exit(0);
}

module.exports.subCompSignUp = async function(res){
 try{
   const block = await web3.eth.getBlockNumber();
   test_contract.events.compSignUpEvent({fromBlock:block, toBlock: 'latest'},async function(err,event){
     if(err){console.log(err);}
     else{ console.log(event.returnValues);
       if(event.returnValues[2]){res.send(event.returnValues);}
       else{res.send("이미 있는 아이디 입니다.");}
     }
   });
 }catch(e){
   console.log(e);
 }
}

module.exports.compLogIn = async function(id,pw){
  try{
     const result = await test_contract.methods.compLogIn(id,pw).call(); //"madcow","iamblackcow"
     return result;
  }catch(e){
    console.log(e);
  }
}

// 업체 예약 관련 함수

// 회원이 예약하면 업체에 알람 해주기


// 업체의 예약 리스트 가져오기
module.exports.compBookList = async function(wallet, compID, date){
  try{
    const block = await web3.eth.getBlockNumber();
    var txCount = await web3.eth.getTransactionCount(wallet,block);
    console.log(txCount);
    const txData = {
      nonce: txCount,//'0x10'
      gasLimit: web3.utils.toHex(3000000),
      gasPrice: web3.utils.toHex(3000000),
      to: contractAddress,
      from: wallet,
      data: test_contract.methods.bookList(compID, date).encodeABI()
    };
    var tx = new Tx(txData,{'chain':'ropsten'});
    tx.sign(privateKey);
    var serialTx = tx.serialize();
    var res = await web3.eth.sendSignedTransaction(`0x${serialTx.toString('hex')}`);
  }catch(e){
    console.log(e);
  }
 // await process.exit(0);
}

module.exports.subCompBookList = async function(res, compID){
 try{
   const block = await web3.eth.getBlockNumber();
   test_contract.events.checkBookListEvent({fromBlock:block, toBlock: 'latest'},async function(err,event){
     if(err){res.send("예약 내역을 불러올 수 없습니다.");}
     else if(event.returnValues[1]==compID){ res.send(event.returnValues); }
     else{res.send("예약 내역을 불러올 수 없습니다.");} // 이벤트 중에 해당 업체의 예약 내역만 고르는 방법은 없을까? => 2학기때 연구해보자.
   });
 }catch(e){
   console.log(e);
 }
}

// 업체가 예약결정 하면 회원에게 결과 알람 해주기

// 전화 예약에 대해 예약결정 하면 회원에게 결과 알람 해주기

// 업체가 노쇼, 쇼업 여부를 Tx에 기록하기
module.exports.compResBook = async function(wallet, keyID, compID, date, isShow){
  try{
    const block = await web3.eth.getBlockNumber();
    var txCount = await web3.eth.getTransactionCount(wallet,block);
    console.log(txCount);
    const txData = {
      nonce: txCount,//'0x10'
      gasLimit: web3.utils.toHex(3000000),
      gasPrice: web3.utils.toHex(3000000),
      to: contractAddress,
      from: wallet,
      data: test_contract.methods.resBook(keyID, compID, date, isShow).encodeABI()
    };
    var tx = new Tx(txData,{'chain':'ropsten'});
    tx.sign(privateKey);
    var serialTx = tx.serialize();
    var res = await web3.eth.sendSignedTransaction(`0x${serialTx.toString('hex')}`);
  }catch(e){
    console.log(e);
  }
 // await process.exit(0);
}

// 승인 대기 예약을 승인, 거부
module.exports.compResBook = async function(wallet, keyID, compID, date, bookState){
  try{
    const block = await web3.eth.getBlockNumber();
    var txCount = await web3.eth.getTransactionCount(wallet,block);
    console.log(txCount);
    const txData = {
      nonce: txCount,//'0x10'
      gasLimit: web3.utils.toHex(3000000),
      gasPrice: web3.utils.toHex(3000000),
      to: contractAddress,
      from: wallet,
      data: test_contract.methods.waitBook(keyID, compID, date, bookState).encodeABI()
    };
    var tx = new Tx(txData,{'chain':'ropsten'});
    tx.sign(privateKey);
    var serialTx = tx.serialize();
    var res = await web3.eth.sendSignedTransaction(`0x${serialTx.toString('hex')}`);
  }catch(e){
    console.log(e);
  }
 // await process.exit(0);
}

// 업체나 고객이 대상 고객의 노쇼 이력 확인하기
module.exports.checkUser = async function(wallet, phoneNumber, callerPN , idx){
  try{
    const block = await web3.eth.getBlockNumber();
    var txCount = await web3.eth.getTransactionCount(wallet,block);
    console.log(txCount);
    const txData = {
      nonce: txCount,//'0x10'
      gasLimit: web3.utils.toHex(3000000),
      gasPrice: web3.utils.toHex(3000000),
      to: contractAddress,
      from: wallet,
      data: test_contract.methods.checkUser(phoneNumber, callerPN , idx).encodeABI()
    };
    var tx = new Tx(txData,{'chain':'ropsten'});
    tx.sign(privateKey);
    var serialTx = tx.serialize();
    var res = await web3.eth.sendSignedTransaction(`0x${serialTx.toString('hex')}`);
  }catch(e){
    console.log(e);
  }
 // await process.exit(0);
}
module.exports.subCheckUser = async function(res, callerPN){
 try{
   const block = await web3.eth.getBlockNumber();
   test_contract.events.checkNoShowListEvent({fromBlock:block, toBlock: 'latest'},async function(err,event){
     if(err){console.log(err);}
     else if(event.returnValues[1]==callerPN){res.send(event.returnValues);}
     else {res.send("없는 전화번호 입니다.");} // 여러 event 중에 callerPN 만 고르는 방법은 없을까? => 2학기때 연구해 보자.
   });
 }catch(e){
   console.log(e);
 }
}
