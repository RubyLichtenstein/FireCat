# FireCat - Firestore real time + pagination recycler view adapter. 

Firestore is great solution for no sql data bases,

When you build chat app you need you db to support pagination for efficient old messages loading and also real time update for new messages. 

Unfortunately [FirebaseUI-Android](https://github.com/firebase/FirebaseUI-Android) provide only 
[Real time](https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md#using-the-firestorerecycleradapter) 
 or [pagination](https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md#using-the-firestorepagingadapter) recycler view adapter.

For uses cases like chat app we need adapter that support both real time and pagination but is no any official implemantion yet :( 

### FireCat for the rescue! with 'FirestoreRealTimePaginationAdapter'
https://github.com/RubyLichtenstein/FireCat/blob/master/app/src/main/java/com/example/firecat/paging/FirestoreRealTimePaginationAdapter.kt

Usage `FirestoreRealTimePaginationAdapter`

```kotlin
class FirestoreRealTimePaginationAdapter(
    paginationQuery: Query,
    realTimeQuery: Query,
    lifecycleOwner: LifecycleOwner?,
    val parser: (DocumentSnapshot) -> T?,
    val prefetchDistance: Int,
    val pageSize: Int
)
```

Chat implemantation with `FirestoreRealTimePaginationAdapter` 

https://github.com/RubyLichtenstein/FireCat/blob/master/app/src/main/java/com/example/firecat/chat/ChatAdapter.kt

```kotlin
class ChatAdapter(
    lifecycleOwner: LifecycleOwner?
) : FirestoreRealTimePaginationAdapter<Message, ChatAdapter.ViewHolder>(
    paginationQuery = messagesQuery,
    realTimeQuery = newMessagesQuery,
    prefetchDistance = 3,
    pageSize = 10,
    parser = { documentSnapshot ->
        documentSnapshot.toObject(Message::class.java)
    },
    lifecycleOwner = lifecycleOwner
)
```
