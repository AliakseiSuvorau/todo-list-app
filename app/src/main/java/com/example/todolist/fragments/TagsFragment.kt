package com.example.todolist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapters.UserTagsAdapter
import com.example.todolist.model.requests.tags.AddTagRequest
import com.example.todolist.model.services.TagService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TagsFragment : Fragment(R.layout.tags_bar) {

    private lateinit var recyclerUserTags: RecyclerView
    private lateinit var addUserTagButton: FloatingActionButton
    private lateinit var userTagsAdapter: UserTagsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerUserTags = view.findViewById(R.id.tags_recycler)
        addUserTagButton = view.findViewById(R.id.add_user_tag_button)

        addUserTagButton.setOnClickListener {
            showAddTagDialog()
        }

        setUserTags()
    }

    private fun setUserTags() {
        val userTags = TagService.getUserTags()
        recyclerUserTags.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        userTagsAdapter = UserTagsAdapter(userTags.toMutableList())
        recyclerUserTags.adapter = userTagsAdapter
    }

    private fun showAddTagDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_tag, null)

        val userTagName = dialogView.findViewById<EditText>(R.id.user_tag_name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New tag")
            .setView(dialogView)
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_all_tasks
                )
            )
            .setPositiveButton("Add") { _, _ ->
                val name = userTagName.text.toString()

                val request = AddTagRequest(name = name)

                val newUserTag = TagService.addTag(request)
                userTagsAdapter.addNewTag(newUserTag)
            }
            .setNegativeButton("Back", null)
            .show()
    }
}