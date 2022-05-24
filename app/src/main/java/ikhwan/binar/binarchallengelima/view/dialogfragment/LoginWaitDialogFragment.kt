package ikhwan.binar.binarchallengelima.view.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import ikhwan.binar.binarchallengelima.R
import ikhwan.binar.binarchallengelima.databinding.FragmentLoginWaitBinding
import ikhwan.binar.binarchallengelima.viewmodel.UserApiViewModel


class LoginWaitDialogFragment : DialogFragment() {

    private var _binding : FragmentLoginWaitBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserApiViewModel by hiltNavGraphViewModels(R.id.nav_main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginWaitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loginStatus.postValue(false)

        viewModel.loginStatus.observe(viewLifecycleOwner){
            if (it){
                dialog?.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}