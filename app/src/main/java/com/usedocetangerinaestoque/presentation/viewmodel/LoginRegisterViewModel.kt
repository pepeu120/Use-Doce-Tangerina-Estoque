package com.usedocetangerinaestoque.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.usedocetangerinaestoque.data.entities.User
import com.usedocetangerinaestoque.usecases.login.LoginUseCase
import com.usedocetangerinaestoque.usecases.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


@HiltViewModel
class LoginRegisterViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<User?>>()
    val loginResult: LiveData<Result<User?>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = User(email = email, password = password)

                loginUseCase(user)
                _loginResult.value = Result.success(user)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = User(name = name, email = email, password = password)

                registerUserUseCase(user)
                _registerResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
        }
    }
}