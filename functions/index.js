const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Listen for changes in all documents and all sub-collections
exports.useWildcard = functions.firestore
    .document('test/{userId}')
    .onUpdate((event) => {


    return admin.database().ref('messages').push({
        name: 'Firebase Bot',
        photoUrl: 'https://image.ibb.co/b7A7Sa/firebase_logo.png', // Firebase logo выгружен на первый попавшийся image hosting
        text: '${fullName} signed in for the first time! Welcome!'
    });



});