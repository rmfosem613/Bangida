const express = require('express');
const pet_jwt = require("../middleware/pet_jwt");
const Calendar = require('../models/Calendar');

const router = express.Router();

// 일정 입력
// method POST
router.post('/', pet_jwt, async (req, res, next) => {
    try{
        const calPlan = await Calendar.create({
            cdate: req.body.cdate,
            cpaln: req.body.cplan,
            animals: req.animals.id
            // pcheck: req.body.pcheck
        });

        if(!calPlan){
            return res.status(400).json({
                success: false,
                msg: "Something went wrong"
            });
        }
        res.status(200).json({
            success: true,
            animals: calPlan,
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
        const cal = await Calendar.find({animals: req.animals.id});

        if(!cal){
            return res.status(400).json({success: false, msg: "error happened"});
        }
        res.status(200).json({success: true, count: cal.length, plans: cal, msg: "Successfully fetched"});
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
        let cal = await Calendar.findById(req.params.id);
        if(!cal){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        cal = await Calendar.findByIdAndUpdate(req.params.id, req.body, {
            new: true,
            runValidators: true
        });

        res.status(200).json({success: true, plans: cal ,msg: "Successfully updated"});

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
        let cal = await Calendar.findById(req.params.id);
        if(!cal){
            return res.status(400).json({success: false, msg: "Task does not exist"});
        }
        cal = await Calendar.findByIdAndDelete(req.params.id);
        
        res.status(200).json({success: true, plans: cal ,msg: "Task Successfully deleted"});

    }
    catch(error){
        next(error);
    }
});

module.exports = router;


