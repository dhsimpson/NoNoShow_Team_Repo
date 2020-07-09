// Web3JS 관련
const Web3 = require('web3');
const ABI = require('../NoNoShow.json');
const Tx = require('ethereumjs-tx').Transaction;
const abi = ABI.abi;
const config = require('../config.json');
const wallet = config.walletAddress
const contractAddress = config.contractAddress
const url = config.url
const web3 = new Web3(new Web3.providers.HttpProvider(url));
const test_contract = new web3.eth.Contract(abi,contractAddress);
const privatekey = config.privateKey
const privateKey = Buffer.from(privatekey,"hex");
const func = require('../functions.js');

web3.eth.accounts.privateKeyToAccount(privateKey);


// app.js 관련
const express = require('express');
const router = express.Router();

// 회원이 예약하면 업체에 알람 해주기

// 업체의 예약 리스트 가져오기
router.post('/compBookList',async function(req,res){
  const body = req.body;
  await func.compBookList(wallet, body.compID,body.date);
  await func.subCompBookList(res, compID);
});
// 업체가 예약결정 하면 회원에게 결과 알람 해주기

// 전화 예약에 대해 예약결정 하면 회원에게 결과 알람 해주기

// 업체가 대기중인 예약을 승인,거부하기
router.post('/waitBook',async function(req,res){ // bytes32 keyID,bytes32 compID, uint32 date, uint32 bookState
  const body = req.body;
  await func.waitBook(wallet, body.keyID, body.compID, body.date, body.bookState);
});

// 업체가 노쇼, 쇼업 여부를 Tx에 기록하기
router.post('/compResBook',async function(req,res){
  const body = req.body;
  await func.compResBook(wallet, body.keyID,body.compID,body.date, body.isShow);
  res.send("기록 완료");
});
// 업체나 고객이 대상 고객의 노쇼 이력 확인하기
router.post('/checkUser',async function(req,res){
  const body = req.body;
  await func.checkUser(wallet, body.phoneNumber,body.callerPN, body.idx);
  await func.subCheckUser(res, body.callerPN);
});


module.exports = router;
