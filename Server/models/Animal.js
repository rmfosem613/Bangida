const mongoose = require('mongoose');

const animalSchema = new mongoose.Schema({
    petname: {
        type: String,
        require: true
    },
    breed: {
        type: String
    },
    birth: {
        type: Date,
        default: Date.now
    },
    etc: {
        type: String
    },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    }
    // petUrl: {
    //     type: String
    // }
});

module.exports = Animal = mongoose.model('animal',animalSchema);