package com.example.firecat.chat

import com.google.firebase.firestore.FirebaseFirestore

val chatQuery by lazy{
    FirebaseFirestore.getInstance().collection("chat")
}
