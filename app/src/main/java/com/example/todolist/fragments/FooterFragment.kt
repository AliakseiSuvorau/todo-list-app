package com.example.todolist.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R

class FooterFragment : Fragment(R.layout.fragment_footer) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonClickListeners(view)
    }

    private fun openFragment(f: Fragment) {
        val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        val sameClass = currentFragment?.javaClass == f.javaClass

        if (!sameClass) {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragment_container, f)
                addToBackStack(null)
            }
        }
    }

    private fun setButtonClickListeners(view: View) {
        view.findViewById<Button>(R.id.all_tasks_list_button).setOnClickListener {
            openFragment(AllTasksFragment())
        }

        view.findViewById<Button>(R.id.current_tasks_list_button).setOnClickListener {
            openFragment(CurrentTasksFragment())
        }

        view.findViewById<Button>(R.id.settings_button).setOnClickListener {
            openFragment(SettingsFragment())
        }
    }
}
