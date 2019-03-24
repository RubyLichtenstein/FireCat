package com.example.firecat.paging

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source

typealias OnLoaded = (querySnapshot: QuerySnapshot, nextPageKey: PageKey) -> Unit

class FirestorePaginationDataSource(
    private val mBaseQuery: Query,
    private val mSource: Source = Source.DEFAULT
) {
    fun loadInitial(
        loadSize: Int,
        onLoaded: OnLoaded
    ) {
        mBaseQuery
            .limit(loadSize.toLong())
            .get(mSource)
            .addOnSuccessListener { querySnapshot ->
                val nextPage = getNextPageKey(querySnapshot)
                onLoaded(querySnapshot, nextPage)
            }
    }

    fun loadMore(
        loadSize: Int,
        key: PageKey,
        onLoaded: OnLoaded
    ) {
        key.getPageQuery(mBaseQuery, loadSize)
            .get(mSource)
            .addOnSuccessListener { querySnapshot ->
                val nextPage = getNextPageKey(querySnapshot)
                onLoaded(querySnapshot, nextPage)
            }
    }

    private fun getNextPageKey(snapshot: QuerySnapshot): PageKey {
        val data = snapshot.documents
        val last = data.lastOrNull()

        return PageKey(last, null)
    }
}
