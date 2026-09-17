package com.liceo.liceochat.data.repository

import com.liceo.liceochat.core.AppResult
import com.liceo.liceochat.data.remote.ChatApiService
import com.liceo.liceochat.data.remote.NewMessageDto
import com.liceo.liceochat.data.remote.toDomain
import com.liceo.liceochat.domain.ChatRepository
import com.liceo.liceochat.domain.Message
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChatRepositoryImpl(
    private val api: ChatApiService
) : ChatRepository {

    override suspend fun getMessages(): AppResult<List<Message>> =
        safeCall { api.getMessages().toDomain() }

    override suspend fun sendMessage(sender: String, text: String): AppResult<Unit> =
        safeCall {
            val dto = NewMessageDto(
                sender = sender,
                text = text,
                createdAt = System.currentTimeMillis()
            )
            api.sendMessage(dto)
            Unit
        }

    private inline fun <T> safeCall(block: () -> T): AppResult<T> =
        try {
            AppResult.Success(block())
        } catch (e: UnknownHostException) {
            AppResult.Failure.NoInternet
        } catch (e: SocketTimeoutException) {
            AppResult.Failure.Timeout
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val message = if (errorBody?.contains("Max number of elements") == true) {
                "Server is full! (Classmates have sent too many messages)"
            } else {
                "HTTP ${e.code()}: ${e.message()}"
            }
            AppResult.Failure.Unknown(message)
        } catch (e: IOException) {
            AppResult.Failure.NoInternet
        } catch (e: Exception) {
            AppResult.Failure.Unknown(e.localizedMessage)
        }
}
