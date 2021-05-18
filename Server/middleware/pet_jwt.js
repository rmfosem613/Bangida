const jwt = require('jsonwebtoken');

module.exports = async function(req, res, next) {
    const token = req.header('Authorization');

    if(!token) {
        return res.status(401).json({
            msg: 'No token, authorizetion denied'
        });
    }

    try {
        await jwt.verify(token, process.env.jwtPetSecret, (err, decoded) => {
            if(err) {
                res.status(401).json({
                    msg: 'Token not valid'
                });
            } else {
                req.animals = decoded.animals;
                next();
            }
        });
    } catch(err) {
        console.log('Someting wend wrong with middleware' + err);
        res.status(500).json({
            msg: 'Server error'
        });
    }
}