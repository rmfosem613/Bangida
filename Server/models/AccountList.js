const mongoose = require('mongoose');

const accountlistSchema = new mongoose.Schema({
    alcheck: {
        type: Boolean,
        default:0
    },
    alcontent: {
        type: String
    },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    }
});

module.exports = AccountList = mongoose.model('accountlist',accountlistSchema);