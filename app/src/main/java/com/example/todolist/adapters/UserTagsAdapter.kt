package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.dtos.tags.Tag
import com.example.todolist.model.services.TagService

class UserTagsAdapter(
    private val userTags: MutableList<Tag>,
): RecyclerView.Adapter<UserTagsAdapter.UserTagViewHolder>() {

    inner class UserTagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button = view.findViewById<ToggleButton>(R.id.user_tag_button)

        fun render(tag: Tag) {
            button.text = tag.name
            button.textOn = tag.name
            button.textOff = tag.name

            button.setOnCheckedChangeListener(null)

            updateButtonBackground(button.isChecked)

            button.setOnCheckedChangeListener { _, isChecked ->
                updateButtonBackground(isChecked)
                handleTagToggle(tag, isChecked)
            }
        }

        private fun handleTagToggle(tag: Tag, isChecked: Boolean) {
            if (isChecked) {
                TagService.addTagToToggled(tag)
            } else {
                TagService.removeTagFromToggled(tag)
            }
        }

        private fun updateButtonBackground(isChecked: Boolean) {
            if (isChecked) {
                button.setBackgroundResource(R.drawable.toggle_button_background_on)
            } else {
                button.setBackgroundResource(R.drawable.toggle_button_background_off)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_tag, parent, false)
        return UserTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserTagViewHolder, position: Int) = holder.render(userTags[position])
    override fun getItemCount() = userTags.size

    fun addNewTag(tag: Tag) {
        userTags.add(tag)
        notifyItemInserted(userTags.size - 1)
    }
}
