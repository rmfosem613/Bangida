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
app.use('/api/bangida/auth', require('./routes/user'));

app.use('/api/animal',require('./routes/animal'))

const PORT = process.env.PORT || 3000;
app.listen(PORT, 
    console.log(`Server running on port : ${PORT}`.red.underline.bold)
    
);