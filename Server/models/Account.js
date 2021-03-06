const mongoose = require('mongoose');

const accountSchema = new mongoose.Schema({
    acdate: {
        type: String,
        require: true
    },
    accontent: {
        type: String
    },
    acprice: {
        type: String
    },
    // etc: {
    //     type: String
    // },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    }
});

module.exports = Account = mongoose.model('account',accountSchema);