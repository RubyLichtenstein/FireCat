package com.example.firecat.paging

import android.arch.lifecycle.*
import android.support.annotation.CallSuper
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import com.google.firebase.firestore.*


abstract class FirestoreRealTimePaginationAdapter<T, VH : RecyclerView.ViewHolder>(
    paginationQuery: Query,
    realTimeQuery: Query,
    lifecycleOwner: LifecycleOwner?,
    val parser: (DocumentSnapshot) -> T?,
    val prefetchDistance: Int,
    val pageSize: Int
) : RecyclerView.Adapter<VH>(), LifecycleObserver {
    abstract val data: SortedList<T>
    val loadingState = MutableLiveData<LoadingState>()

    private val dataSource = FirestorePaginationDataSource(paginationQuery)

    private var newMessagesListenerRegistration: ListenerRegistration? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startListening() {
        loadInitial()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopListening() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun cleanup(source: LifecycleOwner) {
        newMessagesListenerRegistration?.remove()
        source.lifecycle.removeObserver(this)
    }

    init {
        lifecycleOwner?.lifecycle?.addObserver(this)

        newMessagesListenerRegistration = realTimeQuery
            .addSnapshotListener { snapshots: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    loadingState.postValue(LoadingState.ERROR)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty)
                    for (documentChange in snapshots.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val item = parser(documentChange.document)
                                data.add(item)
                                loadingState.postValue(LoadingState.NEW_ITEM)
                                notifyDataSetChanged()
                            }
                            DocumentChange.Type.REMOVED -> {
                            }
                            DocumentChange.Type.MODIFIED -> {
                            }
                        }
                    }
            }
    }

    private fun loadInitial() {
        loadingState.postValue(LoadingState.LOADING_INITIAL)
        dataSource.loadInitial(
            pageSize
        ) { querySnapshot: QuerySnapshot ->
            data.addAll(querySnapshot.documents.map(parser))
            loadingState.value = if (querySnapshot.isEmpty)
                LoadingState.EMPTY
            else
                LoadingState.INITIAL_LOADED
        }
    }

    private fun loadMore() {
        if (dataSource.canLoadMore()) {
            loadingState.postValue(LoadingState.LOADING_MORE)

            dataSource.loadMore(
                pageSize
            ) { querySnapshot: QuerySnapshot ->
                if (querySnapshot.documents.isEmpty()) {
                    loadingState.postValue(LoadingState.FINISHED)
                } else {
                    data.addAll(querySnapshot.documents.map(parser))
                    loadingState.postValue(LoadingState.MORE_LOADED)
                }
            }
        }
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position - prefetchDistance == 0) {
            loadMore()
        }
    }

    override fun getItemCount(): Int = data.size()
}