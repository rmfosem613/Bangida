const mongoose = require('mongoose');

const calendarSchema = new mongoose.Schema({
    cdate: {
        type: String,
        require: true
    },
    sche:  {
        type: String,
        require: true
    },
    pcheck: {
        type: Boolean,
        default: false
    },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    }
});

module.exports = Calendar = mongoose.model('calendar',calendarSchema);