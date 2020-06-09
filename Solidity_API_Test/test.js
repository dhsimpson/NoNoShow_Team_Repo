const Web3 = require('web3');
const ABI = require('./NoNoShow.json');
const abi = ABI.abi; // json 파일안에 있는 Json 객체 abi 를 가져온다
const url = "http://127.0.0.1:7545";
const web3 = new Web3(new Web3.providers.HttpProvider(url));
const wallet = "0x9f9177a5fCf6AA5b7B5Da852fD8b6154c93cF7E8"; // addressFrom
const contractAddress = '0xf46d06E873aFaf660700a511620e1a1AbE6A33E7'; // addressTo
const test_contract = new web3.eth.Contract(abi,contractAddress);


async function signUp(){
  var res = await test_contract.methods.custSignUp("01012345678","haebum","blackcow","12345678",19950509).send({
    from:"0x9f9177a5fCf6AA5b7B5Da852fD8b6154c93cF7E8",
    gasLimit: web3.utils.toHex(3000000),
    gasPrice: web3.utils.toHex(300000),
    to: contractAddress,
  });
  console.log(res);
}


async function logIn(){
  var noShows = await test_contract.methods.custLogIn("blackcow","12345678").call();
  console.log(noShows);
}

logIn();
// const txData = {
//     nonce: '0x09',
//     gasLimit: web3.utils.toHex(3000000),
//     gasPrice: web3.utils.toHex(300000),
//     to: contractAddress,
//     from: wallet
// };
