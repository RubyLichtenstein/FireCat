# FireCat - Firestore real time + pagination recycler view adapter. 

Firestore is great solution for no sql data base with real time updates,

When you build chat app you need also pagination for efficient old messages loading and real time update for new messages. 

Unfortunately [FirebaseUI-Android](https://github.com/firebase/FirebaseUI-Android) provide only 
[Real time](https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md#using-the-firestorerecycleradapter) 
recycler view adapter or [pagination](https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md#using-the-firestorepagingadapter) recycler view adapter, 

For uses cases like chat app we need adapter that support real time and pagination but is no any offical implemantion yet :( 

### FireCat for the rescue! with 'FirestoreRealTimePaginationAdapter'

Usage `FirestoreRealTimePaginationAdapter`

```kotlin
abstract class FirestoreRealTimePaginationAdapter<T, VH : RecyclerView.ViewHolder>(
    paginationQuery: Query,
    realTimeQuery: Query,
    lifecycleOwner: LifecycleOwner?,
    val parser: (DocumentSnapshot) -> T?,
    val prefetchDistance: Int,
    val pageSize: Int
)
```


Chat app example https://github.com/RubyLichtenstein/FireCat/blob/master/app/src/main/java/com/example/firecat/chat/ChatAdapter.kt

```kotlin
```
