package com.example.firecat.paging

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.CallSuper
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import com.google.firebase.firestore.*

data class PagingOptions(
    val prefetchDistance: Int,
    val pageSize: Int
)

abstract class FirestoreRealTimePaginationAdapter<T, VH : RecyclerView.ViewHolder>(
    paginationQuery: Query,
    realTimeQuery: Query,
    val clazz: Class<T>,
    val pagingOptions: PagingOptions
) : RecyclerView.Adapter<VH>() {
    abstract val data: SortedList<T>
    val loadingState = MutableLiveData<LoadingState>()

    private val dataSource = FirestorePaginationDataSource(paginationQuery)
    private var nextPageKey: PageKey? = null
    private val pageSize: Int = pagingOptions.pageSize

    private var newMessagesListenerRegistration: ListenerRegistration? = null

    init {
        loadInitial()
        newMessagesListenerRegistration = realTimeQuery
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                for (documentChange in snapshots!!.documentChanges) {
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            val item = documentChange.document.toObject(clazz)
                            data.add(item)
                            notifyDataSetChanged()
                        }
                        DocumentChange.Type.REMOVED -> {
                        }
                    }
                }
            }
    }

    private fun loadInitial() {
        loadingState.postValue(LoadingState.LOADING_INITIAL)
        dataSource.loadInitial(
            pageSize
        ) { querySnapshot: QuerySnapshot, nextPageKey: PageKey ->
            this.nextPageKey = nextPageKey
            //todo add custom parser
            data.addAll(querySnapshot.documents.map { it.toObject(clazz) })
            loadingState.value = if (querySnapshot.isEmpty)
                LoadingState.EMPTY
            else
                LoadingState.INITIAL_LOADED
        }
    }

    private fun loadMore() {
        loadingState.postValue(LoadingState.LOADING_MORE)

        nextPageKey?.let {
            dataSource.loadMore(
                pageSize,
                it
            ) { querySnapshot: QuerySnapshot, pageKey: PageKey ->

                if (querySnapshot.documents.isEmpty()) {
                    loadingState.postValue(LoadingState.FINISHED)
                    nextPageKey = null
                } else {
                    data.addAll(querySnapshot.documents.map { it.toObject(clazz) })
                    loadingState.postValue(LoadingState.MORE_LOADED)
                    nextPageKey = pageKey
                }
            }
        }
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position + pagingOptions.prefetchDistance == data.size() - 1) {
            loadMore()
        }
    }

    override fun getItemCount(): Int = data.size()
}