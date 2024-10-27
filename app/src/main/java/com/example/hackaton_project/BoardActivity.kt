package com.example.hackaton_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class BoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val profileButton: ImageButton = findViewById(R.id.Profile)
        profileButton.setOnClickListener {
            showProfilePopup()
        }
        val taskList1: LinearLayout = findViewById(R.id.taskList)
        val addTaskButton1: Button = findViewById(R.id.addTask)

        val taskList2: LinearLayout = findViewById(R.id.taskList1)
        val addTaskButton2: Button = findViewById(R.id.addTask1)

        val taskList3: LinearLayout = findViewById(R.id.taskList2)
        val addTaskButton3: Button = findViewById(R.id.addTask2)

        addTaskButton1.setOnClickListener {
            addNewTask(taskList1)
        }
        addTaskButton2.setOnClickListener {
            addNewTask(taskList2)
        }

        addTaskButton3.setOnClickListener {
            addNewTask(taskList3)
        }
    }

    private fun addNewTask(taskList: LinearLayout) {
        val taskCard = LayoutInflater.from(this).inflate(R.layout.task_card, taskList, false)

        val taskTitle: TextView = taskCard.findViewById(R.id.taskTitle)
        val taskDescription: TextView = taskCard.findViewById(R.id.taskDescription)
        val taskAssignee: TextView = taskCard.findViewById(R.id.taskAssignee)

        taskTitle.text = "Новая задача"
        taskDescription.text = "Описание новой задачи"
        taskAssignee.text = "Исполнитель: Иван Иванов"

        taskList.addView(taskCard)
    }

    private fun showProfilePopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_menu, null)

        val profileName = dialogView.findViewById<TextView>(R.id.profileName)
        val profilePosition = dialogView.findViewById<TextView>(R.id.profilePosition)
        val profileRegistrationDate = dialogView.findViewById<TextView>(R.id.profileRegistrationDate)
        val logoutButton = dialogView.findViewById<Button>(R.id.button)

        profileName.text = "Дмитрий Пиэйчпи Бэкендович"
        profilePosition.text = "Должность: Backend"
        profileRegistrationDate.text = "Дата регистрации: 20.11.23"

        // Обработчик кнопки выхода
        logoutButton.setOnClickListener {
            logout()
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun logout() {
        // Удаление токена из SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("authToken")
            apply()
        }

        // Переход на страницу авторизации
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
