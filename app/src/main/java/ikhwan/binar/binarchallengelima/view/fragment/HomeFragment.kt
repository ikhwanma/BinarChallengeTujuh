package ikhwan.binar.binarchallengelima.view.fragment

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ikhwan.binar.binarchallengelima.R
import ikhwan.binar.binarchallengelima.data.datastore.DataStoreManager
import ikhwan.binar.binarchallengelima.data.utils.Status.*
import ikhwan.binar.binarchallengelima.view.adapter.MovieAdapter
import ikhwan.binar.binarchallengelima.view.adapter.MovieLinearAdapter
import ikhwan.binar.binarchallengelima.view.adapter.NowPlayingAdapter
import ikhwan.binar.binarchallengelima.databinding.FragmentHomeBinding
import ikhwan.binar.binarchallengelima.model.nowplaying.ResultNow
import ikhwan.binar.binarchallengelima.model.popularmovie.ResultMovie
import ikhwan.binar.binarchallengelima.viewmodel.MovieApiViewModel
import ikhwan.binar.binarchallengelima.viewmodel.UserApiViewModel
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModelMovie: MovieApiViewModel by hiltNavGraphViewModels(R.id.nav_main)
    private val viewModelUser: UserApiViewModel by hiltNavGraphViewModels(R.id.nav_main)
    private lateinit var pref: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        (activity as AppCompatActivity?)!!.supportActionBar?.title = ""
        pref = DataStoreManager(requireContext())


        viewModelUser.getEmail().observe(viewLifecycleOwner) {
            val email = it

            viewModelUser.getUser(email).observe(viewLifecycleOwner){ user ->
                when (user.status){
                    SUCCESS -> {
                        val data = user.data!![0]
                        (activity as AppCompatActivity?)!!.supportActionBar?.title =
                            "Welcome, ${
                                data.username.replaceFirstChar { userData ->
                                    if (userData.isLowerCase()) userData.titlecase(
                                        Locale.getDefault()
                                    ) else userData.toString()
                                }
                            }!"
                        viewModelUser.user.postValue(data)
                    }
                    ERROR -> Toast.makeText(requireContext(), user.message, Toast.LENGTH_SHORT)
                        .show()
                    LOADING -> Log.d("loadingMsg", "Loading")
                }
            }
        }
        val ai: ApplicationInfo = requireActivity().applicationContext.packageManager
            .getApplicationInfo(
                requireActivity().applicationContext.packageName,
                PackageManager.GET_META_DATA
            )
        val values = ai.metaData["apiKey"]

        viewModelMovie.apiKey.value = values.toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModelMovie.getBoolean().observe(viewLifecycleOwner) {
            val cek = it
            binding.switchRv.isChecked = cek

            viewModelMovie.getPopularMovie().observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        if (cek) {
                            showListLinear(resource.data!!.resultMovies)
                        } else {
                            showList(resource.data!!.resultMovies)
                        }
                        binding.switchRv.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                viewModelMovie.setBoolean(true)
                                showListLinear(resource.data.resultMovies)
                            } else {
                                viewModelMovie.setBoolean(false)
                                showList(resource.data.resultMovies)
                            }
                        }
                        binding.progressCircular.visibility = View.GONE
                    }
                    ERROR -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    LOADING -> {
                        Log.d("loadingMsg", "Loading")
                    }
                }
            }
        }

        viewModelMovie.getNowPlaying().observe(viewLifecycleOwner) {
            when (it.status) {
                SUCCESS -> {
                    showListNowPlay(it.data!!.resultNows)
                }
                ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                LOADING -> {
                    Log.d("loadingMsg", "Loading")
                }
            }
        }
    }

    private fun showListLinear(it: List<ResultMovie>?) {
        binding.rvMovie.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MovieLinearAdapter(object : MovieLinearAdapter.OnClickListener {
            override fun onClickItem(data: ResultMovie) {
                viewModelMovie.id.postValue(data.id)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_detailFragment)
            }
        })
        adapter.submitData(it)
        binding.rvMovie.adapter = adapter
    }

    private fun showListNowPlay(it: List<ResultNow>?) {
        binding.rvNow.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = NowPlayingAdapter(object : NowPlayingAdapter.OnClickListener {
            override fun onClickItem(data: ResultNow) {
                viewModelMovie.id.postValue(data.id)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_detailFragment)
            }
        })
        adapter.submitData(it)
        binding.rvNow.adapter = adapter
    }

    private fun showList(data: List<ResultMovie>?) {
        binding.rvMovie.isNestedScrollingEnabled = false
        binding.rvMovie.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = MovieAdapter(object : MovieAdapter.OnClickListener {
            override fun onClickItem(data: ResultMovie) {
                viewModelMovie.id.postValue(data.id)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_detailFragment)
            }
        })
        adapter.submitData(data)
        binding.rvMovie.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                viewModelUser.user.observe(viewLifecycleOwner) {
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_homeFragment_to_profileFragment2)
                }
                true
            }
            else -> true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}