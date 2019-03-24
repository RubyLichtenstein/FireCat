package com.example.firecat.paging

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source

typealias OnLoaded = (querySnapshot: QuerySnapshot) -> Unit

class FirestorePaginationDataSource(
    private val query: Query,
    private val mSource: Source = Source.DEFAULT
) {
    private var lastDocumentSnapshot: DocumentSnapshot? = null
    fun canLoadMore() = lastDocumentSnapshot != null

    fun loadInitial(
        loadSize: Int,
        onLoaded: OnLoaded
    ) {
        query
            .limit(loadSize.toLong())
            .get(mSource)
            .addOnSuccessListener { querySnapshot ->
                lastDocumentSnapshot = querySnapshot.documents.lastOrNull()
                onLoaded(querySnapshot)
            }
    }

    fun loadMore(
        loadSize: Int,
        onLoaded: OnLoaded
    ) {
        lastDocumentSnapshot?.let {
            query
                .startAfter(it)
                .limit(loadSize.toLong())
                .get(mSource)
                .addOnSuccessListener { querySnapshot ->
                    lastDocumentSnapshot = querySnapshot.documents.lastOrNull()
                    onLoaded(querySnapshot)
                }
        }
    }
}
