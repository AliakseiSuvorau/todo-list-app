package com.example.todolist

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.commit
import com.example.todolist.fragments.AllTasksFragment
import com.example.todolist.fragments.CurrentTasksFragment
import com.example.todolist.fragments.FooterFragment
import com.example.todolist.fragments.HeaderFragment
import com.example.todolist.fragments.MainPageFragment
import com.example.todolist.fragments.SettingsFragment
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
            add(R.id.fragment_container, MainPageFragment())
        }

        // If "back" is pressed then show the main page content
        // If the main page is already shown then exit the app
        onBackPressedDispatcher.addCallback {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment != null && currentFragment is MainPageFragment) {
                finish()
                return@addCallback
            }

            supportFragmentManager.commit {
                replace(R.id.fragment_container, MainPageFragment())
            }
        }

        // Header
        supportFragmentManager.commit {
            replace(R.id.header, HeaderFragment())
        }

        supportFragmentManager.commit {
            replace(R.id.footer, FooterFragment())
        }
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
            openFragmentOnMainPage(AllTasksFragment())
        }

        findViewById<Button>(R.id.current_tasks_list_button).setOnClickListener {
            openFragmentOnMainPage(CurrentTasksFragment())
        }

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            openFragmentOnMainPage(SettingsFragment())
        }
    }

    private fun initDB() {
        db = DatabaseInitializer(this).writableDatabase
        TagRepository.init(db)
        TaskRepository.init(db)
        FilterRepository.init(db)
    }
}
