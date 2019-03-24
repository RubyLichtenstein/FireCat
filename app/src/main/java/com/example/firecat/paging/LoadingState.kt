package com.example.firecat.paging


enum class LoadingState {
    EMPTY,
    LOADING_INITIAL,
    INITIAL_LOADED,
    LOADING_MORE,
    MORE_LOADED,
    FINISHED,
    ERROR,
    NEW_ITEM,
    DELETED_ITEM
}
