const express = require('express');
const user_jwt = require("../middleware/user_jwt");
const Animal = require('../models/Animal');

// 토큰 만들기
const jwt = require('jsonwebtoken');

const router = express.Router();

// 동물 정보 입력
// method POST
router.post('/', user_jwt, async (req, res, next) => {
    try{
        let animals = await Animal.create({
            petname: req.body.petname, 
            breed: req.body.breed,
            birth: req.body.birth, 
            etc: req.body.etc,
            user: req.user.id});

        if(!animals){
            return res.status(400).json({
                success: false,
                msg: "Something went wrong"
            });
        }
        res.status(200).json({success: true, animals: animals, msg: 'Successfully created'});

    } catch(error){
        next(error);
    }
});

// 모든 animlas 받아오기
// method GET
router.get('/', user_jwt, async (req, res, next) => {
    try{
        // 요청
        const pet = await Animal.find({user: req.user.id});

        if(!pet){
            return res.status(400).json({success: false, msg: "error happened"});
        }
        res.status(200).json({success: true, count: pet.length, animals: pet, msg: "Successfully fetched"});
        
    }
    catch(error){
        next(error);
    }
});

// 반려동물 정보 수정하기
// method PUT
router.put('/:id', async(req, res, next) =>{
    try{
        // var params = req.params;
        // var post_id = params.id;
        let animal = await Animal.findById(req.params.id);
        if(!animal){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        animal = await Animal.findByIdAndUpdate(req.params.id, req.body, {
            new: true,
            runValidators: true
        });

        res.status(200).json({success: true, animals: animal ,msg: "Successfully updated"});

    }
    catch(error){
        next(error);
    }   
});

// 동물 정보 삭제
// method Delete
router.delete('/:id', async(req, res, next) =>{
    try{
        // var params = req.params;
        // var post_id = params.id;
        let animal = await Animal.findById(req.params.id);
        if(!animal){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        animal = await Animal.findByIdAndDelete(req.params.id);
        
        res.status(200).json({success: true, animals: animal ,msg: "Task Successfully deleted"});

    }
    catch(error){
        next(error);
    }
});

module.exports = router;


