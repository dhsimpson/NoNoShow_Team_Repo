const Web3 = require('web3');
const Tx = require('ethereumjs-tx').Transaction;
const ABI = require('./NoNoShow.json');
const abi = ABI.abi; // json 파일안에 있는 Json 객체 abi 를 가져온다
const url = "ws://127.0.0.1:7545";//"https://ropsten.infura.io/v3/17ea90b61ac3434699ffcd7d318e50d2";
const provider = new Web3.providers.WebsocketProvider(url);
const web3 = new Web3(provider);
const wallet = "0x64CdF39bd3EB2DFabaA8798342270DF06A4c8dCd";//"0xC29bF73E5819A116544a7513587763add936179b"// addressFrom
const contractAddress = '0x5ccEdE2632701F63a2928554ada704685006269B';//"0x3997D8fA0a6a3691d12C84F0D97b4494fa856549";
const test_contract = new web3.eth.Contract(abi,contractAddress);
const pk = "15896527ce1624fef81faf3158ba7bd75b17cd51922e473a1485dc061a67195c";//"E933BC86D4E136EE9246523CB989FB709F82F9E4B8D87A08A773EA4FB78F888C"
const privateKey = Buffer.from(pk,"hex");
web3.eth.accounts.privateKeyToAccount(privateKey);

/*
async function func(){
  const record = await test_contract.methods.dummy().call();
  console.log(record);
}

func();

async function func2(){
  var balance = await web3.eth.getBalance("0x9f9177a5fCf6AA5b7B5Da852fD8b6154c93cF7E8");
  console.log(balance);
}
func2();
*/
/*
async function func3(){
  await test_contract.methods.setDMap("01062126262").send({from:"0x9f9177a5fCf6AA5b7B5Da852fD8b6154c93cF7E8"});
}
func3();

async function func4(){
  var noShows = await test_contract.methods.daRet("01062126262").call();
  console.log(noShows);
}
func4();
*/

async function logIn2(){
    const txData = {
      data: test_contract.methods.custLogIn("dhsimpson","13fkdlqm13").encodeABI()
    };
    var tx = new Tx(txData,{'chain':'ropsten'});
    tx.sign(privateKey);
    var serialTx = tx.serialize();
    const res = await test_contract.methods.custLogIn("blackcow","12345678").call();
    console.log(res);
}



// signUpPromise
var signUp_P = function(wallet,PN,name,id,pw,birth){
  return new Promise(function(resolve, reject){

  })
}



async function signUp(){
  const block = await web3.eth.getBlockNumber();
  var txCount = await web3.eth.getTransactionCount(wallet,block);
  console.log(txCount);
  const txData = {
    nonce: txCount,//'0x10'
    gasLimit: web3.utils.toHex(3000000),
    gasPrice: web3.utils.toHex(3000000),
    to: contractAddress,
    from: wallet,
    // data: test_contract.methods.custSignUp("01012345678","haebum","blackcow","12345678",19950509).encodeABI()
    // data: test_contract.methods.custSignUp("01062126262","donghee","kingDonghee","12345678",19940407).encodeABI()
    // data: test_contract.methods.custSignUp("01059595959","kanghee","blackblack","12345678",19940407).encodeABI()
    data: test_contract.methods.custSignUp("01059595882","bomhae","cacow","12345678",19940407).encodeABI()
  };
  var tx = new Tx(txData,{'chain':'ropsten'});
  tx.sign(privateKey);
  var serialTx = tx.serialize();
  var res = await web3.eth.sendSignedTransaction(`0x${serialTx.toString('hex')}`);
  // console.log(res);
  // provider.on('error', e=> handleDisconnects(e));
  // provider.on('end', e=> handleDisconnects(e));

  await process.exit(0);
}
/*** Call Functions ***/

async function signUPEvent(){
  await test_contract.events.custSignUpEvent({fromBlock:0, toBlock: 'latest'},async function(err,event){
    if(err){console.log(err);}
    else{ console.log(event.returnValues);}
  });
}
signUp();
var a = signUPEvent();
// logIn2();

function handleDisconnects(e){
  console.log("error",e);
  web3.setProvider(url);
}
