package ikhwan.binar.binarchallengelima.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ikhwan.binar.binarchallengelima.data.datastore.DataStoreManager
import ikhwan.binar.binarchallengelima.data.room.Favorite
import ikhwan.binar.binarchallengelima.model.users.GetUserResponseItem
import ikhwan.binar.binarchallengelima.model.users.PostUserResponse
import ikhwan.binar.binarchallengelima.data.utils.MainRepository
import ikhwan.binar.binarchallengelima.data.utils.Resource
import ikhwan.binar.binarchallengelima.data.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UserApiViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val pref: DataStoreManager
) : ViewModel() {


    val user = MutableLiveData<GetUserResponseItem>()
    val loginStatus = SingleLiveEvent<Boolean>()
    val registerCheck = SingleLiveEvent<Boolean>()
    val listUser = MutableLiveData<List<GetUserResponseItem>>()
    val loginCheck = SingleLiveEvent<Boolean>()

    fun setEmail(email: String) {
        viewModelScope.launch {
            pref.setUser(email)
        }
    }

    fun getEmail(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun setImage(img: String) {
        viewModelScope.launch {
            pref.setImageCamera(img)
        }
    }

    fun getImage(): LiveData<String> {
        return pref.getImage().asLiveData()
    }

    fun getUser(email: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mainRepository.getUser(email)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occured!"))
        }
    }

    fun registerUser(user: PostUserResponse) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mainRepository.addUsers(user)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occured!"))
        }
    }

    fun updateUser(user: PostUserResponse, id: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mainRepository.updateUser(user, id)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occured!"))
        }
    }

    fun addFavorite(favorite: Favorite) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.addFavorite(favorite)
    }

    fun deleteFavorite(favorite: Favorite) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.deleteFavorite(favorite)
    }

    fun getFavorite(email: String) = mainRepository.getFavorite(email)
}