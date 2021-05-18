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
        type: Number,
        default: 0
    },
    // etc: {
    //     type: String
    // },
    animals: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Animal'
    }
});

module.exports = Account = mongoose.model('account',accountSchema);