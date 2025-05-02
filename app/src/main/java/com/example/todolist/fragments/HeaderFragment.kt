package com.example.todolist.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R

class HeaderFragment : Fragment(R.layout.fragment_header) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.about_app_button).setOnClickListener {
            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
            val appInfoFragment = AppInfoFragment()
            val sameClass = currentFragment?.javaClass == appInfoFragment.javaClass

            if (!sameClass) {
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.fragment_container, AppInfoFragment())
                    addToBackStack(null)
                }
            }
        }
    }
}
