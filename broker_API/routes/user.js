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

web3.eth.accounts.privateKeyToAccount(privateKey);


// app.js 관련
const express = require('express');
const router = express.Router();

// router.post('/',function(req,res){
//   console.log(req.body);
//   // res.json(req);
//   res.status(400).json({message: 'Hey, Bad request!!!'});
// });
//
// router.get('/:id',function(req,res){
//   res.send('Received a GET request, param:'+req.params.id);
// });

// 고객
router.post('/custSignUp',async function(req,res){
  const body = req.body;
  await func.custSignUp(wallet, body.phoneNumber,body.name,body.id,body.pw,body.birth);
  await func.subCUstSignIn(res);
});

router.post('/custLogIn',async function(req,res){
  var result = await func.custLogIn(req.body.id, req.body.pw);
  res.send(result);
});

// 업체
router.post('/compSignUp',async function(req,res){
  const body = req.body;
  await func.compSignUp(wallet, body.name, body.startTime, body.id, body.pw, body.address, body.phoneNumber,body.img,body.description);
  await func.subCUstSignIn(res);
});

router.post('/compLogIn',async function(req,res){
  var result = await func.compLogIn(req.body.id, req.body.pw);
  res.send(result);
});






module.exports = router;
