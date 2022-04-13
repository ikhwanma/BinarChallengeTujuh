package ikhwan.binar.binarchallengelima.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import ikhwan.binar.binarchallengelima.database.User
import ikhwan.binar.binarchallengelima.database.UserDatabase
import kotlinx.coroutines.*
import okhttp3.Dispatcher

class RegisterViewModel : ViewModel() {

    private var userDatabase: UserDatabase? = null

    fun setUserDb(userDatabase: UserDatabase) {
        this.userDatabase = userDatabase
    }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _registerStatus = MutableLiveData<Boolean>()
    val registerStatus: LiveData<Boolean> = _registerStatus

    fun userRegister(user: User, email: String) {
        GlobalScope.async {
            val cekUser = userDatabase?.userDao()?.getUserRegistered(email)
            if (cekUser != null) {
                _toastMessage.postValue("User dengan email ${user.email} sudah terdaftar")
            } else {
                val result = userDatabase?.userDao()?.registerUser(user)
                if (result != 0.toLong()) {
                    _toastMessage.postValue("Sukses mendaftarkan ${user.email}, silakan mencoba untuk login")
                    _registerStatus.postValue(true)
                } else {
                    _toastMessage.postValue("Gagal mendaftarkan ${user.email}, silakan coba lagi")
                }
            }
        }

    }
}