package com.example.firecat.chat

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firecat.paging.FirestoreRealTimePaginationAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.chat_message_layout.view.*
import java.util.*


fun Query.whereAfterTimestamp(): Query =
    whereGreaterThan("timestamp", Timestamp.now())

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
) {

    override val data: SortedList<Message> = SortedList<Message>(
        Message::class.java,
        object : SortedListAdapterCallback<Message>(this) {
            override fun compare(a: Message, b: Message): Int =
                a.timestamp.compareTo(b.timestamp)

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.content == newItem.content

            override fun areItemsTheSame(item1: Message, item2: Message): Boolean =
                item1.id == item2.id
        })

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.example.firecat.R.layout.chat_message_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bindData(data[position])
    }

    var longClick: ((Message) -> Unit)? = null

    inner class ViewHolder(private val containerView: View) :
        RecyclerView.ViewHolder(containerView) {
        fun bindData(message: Message) {
            containerView.messageContent.text = message.content
            containerView.messageTimestamp.text = formatTimestamp(message.timestamp.toDate())
            containerView.messageListItem.setOnLongClickListener {
                longClick?.invoke(message)
                true
            }
        }

        private fun formatTimestamp(date: Date): String {
            return android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", date).toString()
        }
    }
}