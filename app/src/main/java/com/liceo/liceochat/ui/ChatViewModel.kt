package com.liceo.liceochat.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.liceo.liceochat.core.AppResult
import com.liceo.liceochat.data.remote.NetworkModule
import com.liceo.liceochat.data.repository.ChatRepositoryImpl
import com.liceo.liceochat.domain.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    var uiState by mutableStateOf<ChatUiState>(ChatUiState.Loading)
        private set

    var myName by mutableStateOf("")
        private set

    var draft by mutableStateOf("")
        private set

    init {
        load()
    }

    fun onNameChange(newName: String) {
        myName = newName
    }

    fun onDraftChange(newDraft: String) {
        draft = newDraft
    }

    fun load() {
        viewModelScope.launch {
            uiState = ChatUiState.Loading
            when (val result = repository.getMessages()) {
                is AppResult.Success -> {
                    if (result.data.isEmpty()) {
                        uiState = ChatUiState.Empty
                    } else {
                        uiState = ChatUiState.Ready(result.data)
                    }
                }
                is AppResult.Failure -> {
                    val errorMsg = when (result) {
                        AppResult.Failure.NoInternet -> "No internet connection."
                        AppResult.Failure.Timeout -> "The server took too long."
                        is AppResult.Failure.Unknown -> result.message ?: "Something went wrong."
                    }
                    uiState = ChatUiState.Error(errorMsg)
                }
            }
        }
    }

    fun send() {
        if (myName.isBlank() || draft.isBlank()) return

        val textToSend = draft
        draft = "" // Clear input field immediately for better UX

        viewModelScope.launch {
            when (val result = repository.sendMessage(myName, textToSend)) {
                is AppResult.Success -> {
                    load() // Refresh message list after successful post
                }
                is AppResult.Failure -> {
                    draft = textToSend // Restore draft if post fails
                    val errorMsg = when (result) {
                        AppResult.Failure.NoInternet -> "No internet connection."
                        AppResult.Failure.Timeout -> "The server took too long."
                        is AppResult.Failure.Unknown -> result.message ?: "Could not send."
                    }
                    uiState = ChatUiState.Error(errorMsg)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val api = NetworkModule.chatApi
                val repository = ChatRepositoryImpl(api)
                ChatViewModel(repository)
            }
        }
    }
}