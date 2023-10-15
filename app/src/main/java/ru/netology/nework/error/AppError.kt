package ru.netology.nework.error

import android.database.SQLException
import android.util.Log
import java.io.IOException

sealed class AppError(var code: String) : RuntimeException() {
    companion object {
        fun from(e: Throwable): AppError {
            Log.d("debug", e.message.toString())
            return when (e) {
                is AppError -> e
                is SQLException -> DbError
                is IOException -> NetworkError
                else -> UnknownError
            }
        }
    }
}

class ApiError(val status: Int, code: String) : AppError(code)
object NetworkError : AppError("error_network")
object DbError : AppError("error_db")
object UnknownError : AppError("error_unknown")