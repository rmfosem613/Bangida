const express = require('express');
const colors = require('colors');
const morgan = require('morgan');
const dotenv = require('dotenv');
const connectDB = require('./config/db');

const app = express();

app.use(morgan('dev'));

app.use(express.json({}));
app.use(express.json({extended:true}))

dotenv.config({
    path: './config/config.env'
});

connectDB();

// https://bangidaapp.herokuapp.com/api/bangida/auth/register

// 사용자 정보
app.use('/api/bangida/auth', require('./routes/user'));
// 동물 정보
app.use('/api/animal',require('./routes/animal'));
// 달력 일정
app.use('/api/calendar',require('./routes/calendar'));
// 가계부 정보
app.use('/api/account',require('./routes/account'));
// 가계부 체크 리스트 정보
app.use('/api/accountlist',require('./routes/accountlist'));

const PORT = process.env.PORT || 3000;
app.listen(PORT, 
    console.log(`Server running on port : ${PORT}`.red.underline.bold)
    
);