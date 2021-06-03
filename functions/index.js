
// // The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
// const functions = require('firebase-functions');

// // The Firebase Admin SDK to access Firestore.
// const admin = require('firebase-admin');
// admin.initializeApp();

// // Take the text parameter passed to this HTTP endpoint and insert it into 
// // Firestore under the path /messages/:documentId/original
// exports.addMessage = functions.https.onRequest(async (req, res) => {
//   // Grab the text parameter.
//   const original = req.query.text;
//   // Push the new message into Firestore using the Firebase Admin SDK.
//   const writeResult = await admin.firestore().collection('messages').add({original: original});
//   // Send back a message that we've successfully written the message
//   res.json({result: `Message with ID: ${writeResult.id} added.`});
// });

// // Listens for new messages added to /messages/:documentId/original and creates an
// // uppercase version of the message to /messages/:documentId/uppercase
// exports.makeUppercase = functions.firestore.document('/messages/{documentId}')
//     .onCreate((snap, context) => {
//       // Grab the current value of what was written to Firestore.
//       const original = snap.data().original;

//       // Access the parameter `{documentId}` with `context.params`
//       functions.logger.log('Uppercasing', context.params.documentId, original);
      
//       const uppercase = original.toUpperCase();
      
//       // You must return a Promise when performing asynchronous tasks inside a Functions such as
//       // writing to Firestore.
//       // Setting an 'uppercase' field in Firestore document returns a Promise.
//       return snap.ref.set({uppercase}, {merge: true});
//     });



// const vision = require('@google-cloud/vision');
// const visionClient =  new vision.ImageAnnotatorClient();
// // const vision = Vision();
// const functions = require('firebase-functions');
// const admin = require("firebase-admin");
// admin.initializeApp(functions.config().firebase);

// exports.callVision = functions.storage.object().onFinalize(event => {
//     const object = event.data;
//     const fileBucket = object.bucket;
//     const filePath = object.name;
//     const gcsPath = `gs://${fileBucket}/${filePath}`;

//     // Prepare the request object
//     var req = {
//       image: {source: {imageUri: gcsPath}},
//       features: [{ type: Vision.v1.types.Feature.Type.WEB_DETECTION },
//       { type: Vision.v1.types.Feature.Type.SAFE_SEARCH_DETECTION }],
//     };
//     console.log('vision api image ' + gcsPath);
//     // Call the Vision API's web detection and safe search detection endpoints
//     return visionClient.annotateImage(req).then(response => {
//         let webDetection = response[0].webDetection;
//         let safeSearch = response[0].safeSearchAnnotation;
//         return {web: webDetection, safe: safeSearch};
//     }).then((visionResp) => {
//         var db = admin.firestore();
//         let imageRef = db.collection('images').doc(filePath.slice(7));
//         return imageRef.set(visionResp);
//     })
//     .catch(err => {
//         console.log('vision api error', err);
//     });

// });

"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.annotateImage = void 0;
const functions = require("firebase-functions");
const vision_1 = require("@google-cloud/vision");
const client = new vision_1.default.ImageAnnotatorClient();
// This will allow only requests with an auth token to access the Vision
// API, including anonymous ones.
// It is highly recommended to limit access only to signed-in users. This may
// be done by adding the following condition to the if statement:
//    || context.auth.token?.firebase?.sign_in_provider === 'anonymous'
//
// For more fine-grained control, you may add additional failure checks, ie:
//    || context.auth.token?.firebase?.email_verified === false
// Also see: https://firebase.google.com/docs/auth/admin/custom-claims
exports.annotateImage = functions.https.onCall(async (data, context) => {
    console.log("DATA: " + data);
    if (!context.auth) {
        throw new functions.https.HttpsError("unauthenticated", "annotateImage must be called while authenticated.");
    }
    try {
        return await client.annotateImage(JSON.parse(data));
    }
    catch (e) {
        throw new functions.https.HttpsError("internal", e.message, e.details);
    }
});