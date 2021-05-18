const express = require('express');
const pet_jwt = require("../middleware/pet_jwt");
const Account = require('../models/Account');

const router = express.Router();

// 일정 입력
// method POST
router.post('/', pet_jwt, async (req, res, next) => {
    try{
        const account = await Account.create({
            acdate: req.body.acdate,
            accontent: req.body.accontent,
            acprice: req.body.acprice,
            animals: req.animals.id
        });

        if(!account){
            return res.status(400).json({
                success: false,
                msg: "Something went wrong"
            });
        }
        res.status(200).json({
            success: true,
            contents: account,
            msg: 'Successfully created'
        });
    } catch(error){
        next(error);
    }
});

// 모든 일정 받아오기
// method GET
router.get('/', pet_jwt, async (req, res, next) => {
    try{
        const account = await Account.find({animals: req.animals.id});

        if(!account){
            return res.status(400).json({success: false, msg: "error happened"});
        }
        res.status(200).json({success: true, count: account.length, contents: account, msg: "Successfully fetched"});
    }
    catch(error){
        next(error);
    }
});



// 일정 수정하기
// method PUT
router.put('/:id', async(req, res, next) =>{
    try{
        // var params = req.params;
        // var post_id = params.id;
        let account = await Account.findById(req.params.id);
        if(!account){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        account = await Account.findByIdAndUpdate(req.params.id, req.body, {
            new: true,
            runValidators: true
        });

        res.status(200).json({success: true, contents: account ,msg: "Successfully updated"});

    }
    catch(error){
        next(error);
    }   
});


// 일정 삭제
// method Delete
router.delete('/:id', async(req, res, next) =>{
    try{
        // var params = req.params;
        // var post_id = params.id;
        let account = await Account.findById(req.params.id);
        if(!account){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        account = await Account.findByIdAndDelete(req.params.id);
        
        res.status(200).json({success: true, contents: account ,msg: "Task Successfully deleted"});

    }
    catch(error){
        next(error);
    }
});

module.exports = router;


