package com.example.firecat.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.firecat.R
import com.example.firecat.paging.LoadingState
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.chat_layout.*
import java.util.*

class ChatFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ChatAdapter()

        messagesList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            true
        ).apply {
            stackFromEnd = true
        }

        messagesList.adapter = adapter


        adapter.loadingState.observe(this, android.arch.lifecycle.Observer {
            if (it != null) {
                handleLoadingState(it)
            }
        })

        setupSendMessage()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.chat_layout, container, false)


    private fun handleLoadingState(loadingState: LoadingState) {
        Toast.makeText(activity, loadingState.name, LENGTH_SHORT).show()

        when (loadingState) {
            LoadingState.EMPTY -> renderView(empty = true)
            LoadingState.LOADING_INITIAL -> renderView(loading = true)
            LoadingState.INITIAL_LOADED -> renderView(messages = true)
            LoadingState.LOADING_MORE -> renderView(loading = true, messages = true)
            LoadingState.MORE_LOADED -> renderView(messages = true)
            LoadingState.FINISHED -> renderView(messages = true)
            LoadingState.ERROR -> renderView(error = true)
        }
    }

    private fun renderView(
        empty: Boolean = false,
        loading: Boolean = false,
        error: Boolean = false,
        messages: Boolean = false
    ) {
        errorText.show(error)
        progressBar.show(loading)
        emptyStateText.show(empty)
        messagesList.show(messages)
    }

    private fun setupSendMessage() {
        sendButton.setOnClickListener {
            val message = commentInputEditText.text.toString()
            if (message.isEmpty())
                return@setOnClickListener

            val id = UUID.randomUUID().toString()
            chatQuery
                .document(id)
                .set(
                    Message(
                        id = UUID.randomUUID().toString(),
                        content = message,
                        timestamp = Timestamp.now()
                    )
                )
            commentInputEditText.setText("")
        }
    }
}

private fun View.show(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}
