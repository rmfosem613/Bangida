const express = require('express');
const user_jwt = require("../middleware/user_jwt");
const AccountList = require('../models/AccountList');

const router = express.Router();

// 일정 입력
// method POST
router.post('/', user_jwt, async (req, res, next) => {
    try{
        const acclist = await AccountList.create({
            alcontent: req.body.alcontent,
            user: req.user.id
        });

        if(!acclist){
            return res.status(400).json({
                success: false,
                msg: "Something went wrong"
            });
        }
        res.status(200).json({
            success: true,
            contents: acclist,
            msg: 'Successfully created'
        });
    } catch(error){
        next(error);
    }
});

// 모든 일정 받아오기
// method GET
router.get('/', user_jwt, async (req, res, next) => {
    try{
        const acclist = await AccountList.find({user: req.user.id});

        if(!acclist){
            return res.status(400).json({success: false, msg: "error happened"});
        }
        res.status(200).json({success: true, count: acclist.length, contents: acclist, msg: "Successfully fetched"});
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
        let acclist = await AccountList.findById(req.params.id);
        if(!acclist){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        acclist = await AccountList.findByIdAndUpdate(req.params.id, req.body, {
            new: true,
            runValidators: true
        });

        res.status(200).json({success: true, contents: acclist ,msg: "Successfully updated"});

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
        let acclist = await AccountList.findById(req.params.id);
        if(!acclist){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        acclist = await AccountList.findByIdAndDelete(req.params.id);
        
        res.status(200).json({success: true, contents: acclist ,msg: "Task Successfully deleted"});

    }
    catch(error){
        next(error);
    }
});

module.exports = router;


