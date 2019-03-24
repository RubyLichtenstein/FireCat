package com.example.firecat.chat

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

val chatCollectionReference by lazy {
    FirebaseFirestore
        .getInstance()
        .collection("chat")
}

val messagesQuery by lazy {
    chatCollectionReference
        .orderBy("timestamp", Query.Direction.DESCENDING)
}

val newMessagesQuery by lazy {
    messagesQuery.whereAfterTimestamp()
}