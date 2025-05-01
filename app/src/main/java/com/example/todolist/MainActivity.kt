package com.example.todolist

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.commit
import com.example.todolist.model.DatabaseInitializer
import com.example.todolist.model.repositories.FilterRepository
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.repositories.TaskRepository

class MainActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDB()

        // Initial main page content
        supportFragmentManager.commit {
            add(R.id.fragment_container, MainPage())
        }

        // If "back" is pressed then show the main page content
        // If the main page is already shown then exit the app
        onBackPressedDispatcher.addCallback {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment != null && currentFragment is MainPage) {
                finish()
                return@addCallback
            }

            supportFragmentManager.commit {
                replace(R.id.fragment_container, MainPage())
            }
        }

        setButtonClickListeners()
    }

    private fun openFragmentOnMainPage(f: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        val sameClass = currentFragment?.javaClass == f.javaClass

        if (!sameClass) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, f)
                addToBackStack(null)
            }
        }
    }

    private fun setButtonClickListeners() {
        findViewById<Button>(R.id.all_tasks_list_button).setOnClickListener {
            openFragmentOnMainPage(AllTasksPage())
        }

        findViewById<Button>(R.id.current_tasks_list_button).setOnClickListener {
            openFragmentOnMainPage(CurrentTasksPage())
        }

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            openFragmentOnMainPage(SettingsPage())
        }
    }

    private fun initDB() {
        db = DatabaseInitializer(this).writableDatabase
        TagRepository.init(db)
        TaskRepository.init(db)
        FilterRepository.init(db)
    }
}
