package com.example.firecat.paging

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class PageKey(
    private val mStartAfter: DocumentSnapshot?,
    private val mEndBefore: DocumentSnapshot?
) {

    fun getPageQuery(baseQuery: Query, size: Int): Query {
        var pageQuery = baseQuery

        if (mStartAfter != null) {
            pageQuery = pageQuery.startAfter(mStartAfter)
        }

        pageQuery = if (mEndBefore != null) {
            pageQuery.endBefore(mEndBefore)
        } else {
            pageQuery.limit(size.toLong())
        }

        return pageQuery
    }

    override fun toString(): String {
        val startAfter = mStartAfter?.id
        val endBefore = mEndBefore?.id
        return "PageKey{StartAfter=$startAfter, EndBefore=$endBefore}"
    }
}
