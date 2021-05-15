const express = require('express');
const auth = require("../middleware/user_jwt");
const Animal = require('../models/Animal')

const router = express.Router();

// desc Create new animal task
// method POST
router.post('/', auth, async (req, res, next) => {
    try{
        const animalInfo = await Animal.create({
            petname: req.body.petname, 
            breed: req.body.breed,
            birth: req.body.birth, 
            etc: req.body.etc,
            user: req.user.id});

        if(!animalInfo){
            return res.status(400).json({
                success: false,
                msg: "Something went wrong"
            });
        }
        res.status(200).json({
            success: true,
            animal: animalInfo,
            msg: 'Successfully created'
        });
    } catch(error){
        next(error);
    }
})

module.exports = router;

