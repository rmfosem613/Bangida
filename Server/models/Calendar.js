const mongoose = require('mongoose');

const calendarSchema = new mongoose.Schema({
    cdate: {
        type: Date,
        default: Date.now
    },
    cplan: {
        type: String
    },
    pcheck: {
        type: Boolean,
        default: false
    },
    animals: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Animal'
    }
});

module.exports = Calendar = mongoose.model('calendar',calendarSchema);