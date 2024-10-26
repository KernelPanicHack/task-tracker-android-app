package com.example.hackaton_project

import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_board)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.boardid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val profileBut: ImageButton = findViewById(R.id.Profile)
        profileBut.setOnClickListener { showPopupMenu(it) }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.filter_by_date -> {
                    filterByDate()
                    true
                }
                R.id.filter_by_responsible -> {
                    filterByResponsible()
                    true
                }
                R.id.filter_by_keywords -> {
                    filterByKeywords()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun filterByDate() {
    }

    private fun filterByResponsible() {
    }

    private fun filterByKeywords() {
    }
}
