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
        type: String
    },
    etc: {
        type: String
    },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
    }
});

module.exports = Animal = mongoose.model('animal',animalSchema);