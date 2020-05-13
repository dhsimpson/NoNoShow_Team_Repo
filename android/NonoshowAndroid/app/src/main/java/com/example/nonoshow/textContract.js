const Web3 = require('web3');
const ABI = require('./NoNoShow.json');
const abi = ABI; // json 파일안에 있는 Json 객체 abi 를 가져온다
const url = 'http://127.0.0.1:7545';
const web3 = new Web3(url);
const contractAddress = '0x884829e64A5D52653A84d1822650A61eb2E7aA7A';
const test_contract = new web3.eth.Contract(abi,contractAddress);
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
async function func3(){
  await test_contract.methods.setDMap("01062126262").send({from:"0x9f9177a5fCf6AA5b7B5Da852fD8b6154c93cF7E8"});
}
func3();

async function func4(){
  var noShows = await test_contract.methods.daRet("01062126262").call();
  console.log(noShows);
}
func4();