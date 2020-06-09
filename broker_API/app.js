// Web3JS 관련
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
const func = require('./functions.js');

web3.eth.accounts.privateKeyToAccount(privateKey);

// App.js 관련
const express = require('express');
const app = express();
const user = require('./routes/user');
const book = require('./routes/book');

app.listen(4242, function(){
  console.log('Broker API is listening on port 4242');
});
app.use(express.json());
app.use('/user',user);
app.use('/book',book);
// app.post('/test',function(req,res){
//   console.log(req.body);
//   res.send("Hello");
// });
// app.get('/',function(req,res){
//   res.send('Hello Wolrd');
// });
