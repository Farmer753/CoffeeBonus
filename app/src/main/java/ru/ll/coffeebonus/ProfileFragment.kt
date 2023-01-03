package ru.ll.coffeebonus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.ll.coffeebonus.databinding.FragmentProfileBinding
import timber.log.Timber

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding =
        FragmentProfileBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { Timber.d("Вывод из profileFragment") }
        binding.buttonMap.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_map)
        }
    }

}