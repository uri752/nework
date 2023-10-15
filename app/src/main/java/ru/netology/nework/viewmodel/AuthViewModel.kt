package ru.netology.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.TypeAttachment
import ru.netology.nework.model.AuthModel
import ru.netology.nework.model.AuthModelState
import ru.netology.nework.model.MediaModel
import ru.netology.nework.repositry.auth.AuthRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(

    private val auth: AppAuth,
    private val repository: AuthRepository
) : ViewModel() {
    val data: LiveData<AuthModel> = auth.authStateFlow
        .asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = auth.authStateFlow.value.id != 0L

    private val _state = MutableLiveData(AuthModelState())
    val state: LiveData<AuthModelState>
        get() = _state

    private val _mediaAvatar = MutableLiveData<MediaModel?>(null)
    val mediaAvatar: LiveData<MediaModel?>
        get() = _mediaAvatar


    fun authorization(login: String, pass: String) {
        viewModelScope.launch {
            try {
                repository.authorization(login, pass)
                _state.value = AuthModelState(authorized = true)
            } catch (e: Exception) {
                _state.value = AuthModelState(errorCode = true)
            }

        }
    }

    fun registration(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                when (val media = mediaAvatar.value) {
                    null -> repository.registration(login, password, name)
                    else -> repository.registrationWithAvatar(login, password, name, media)
                }
                _state.value = AuthModelState(authorized = true)
            } catch (e: Exception) {
                _state.value = AuthModelState(errorCode = true)
            }
        }
    }

    fun changeAvatar(file: File, uri: Uri, type: TypeAttachment) {
        _mediaAvatar.value = MediaModel(uri, file, type)
    }

    fun clearAvatar() {
        _mediaAvatar.value = null
    }
}