package com.example.todolist

import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        supportFragmentManager.commit {
            replace(R.id.fragment_container, f)
        }
    }

    private fun setButtonClickListeners() {
        findViewById<Button>(R.id.all_tasks_list_button).setOnClickListener {
            openFragmentOnMainPage(AllTasksList())
        }

        findViewById<Button>(R.id.current_tasks_list_button).setOnClickListener {
            openFragmentOnMainPage(CurrentTasksList())
        }

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            openFragmentOnMainPage(Settings())
        }
    }
}
