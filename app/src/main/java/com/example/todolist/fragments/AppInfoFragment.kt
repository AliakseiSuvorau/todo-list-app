package com.example.todolist.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.todolist.R

class AppInfoFragment : Fragment(R.layout.fragment_about_app) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tgLink = view.findViewById<TextView>(R.id.tg_link)
        tgLink.movementMethod = LinkMovementMethod.getInstance()

        val githubLink = view.findViewById<TextView>(R.id.github_link)
        githubLink.movementMethod = LinkMovementMethod.getInstance()

        view.findViewById<Button>(R.id.back_button).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
