package com.example.hackaton_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class BoardActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var authToken: String? = null

    private lateinit var taskListPending: LinearLayout
    private lateinit var taskListInProgress: LinearLayout
    private lateinit var taskListCompleted: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        authToken = intent.getStringExtra("TOKEN")

        taskListPending = findViewById(R.id.taskListPending)
        taskListInProgress = findViewById(R.id.taskListInProgress)
        taskListCompleted = findViewById(R.id.taskListCompleted)

        val profileButton: ImageButton = findViewById(R.id.Profile)
        profileButton.setOnClickListener {
            showProfilePopup()
        }

        fetchTasks()
    }

    // Перечисление для статусов задач
    enum class TaskStatus(val status: String) {
        PENDING("В ожидании"),
        IN_PROGRESS("В процессе"),
        COMPLETED("Выполнено");

        companion object {
            fun from(status: String?): TaskStatus {
                return when (status) {
                    IN_PROGRESS.status -> IN_PROGRESS
                    COMPLETED.status -> COMPLETED
                    else -> PENDING
                }
            }
        }
    }

    private fun fetchTasks() {
        val url = "https://356d-188-162-172-157.ngrok-free.app/api/tasks"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handleError("Ошибка загрузки задач")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    val tasksArray = JSONArray(responseData)
                    runOnUiThread {
                        populateTaskLists(tasksArray)
                    }
                } else {
                    handleError("Ошибка загрузки задач")
                }
            }
        })
    }

    private fun populateTaskLists(tasksArray: JSONArray) {
        // Очистка всех списков перед заполнением
        taskListPending.removeAllViews()
        taskListInProgress.removeAllViews()
        taskListCompleted.removeAllViews()

        var pendingCount = 0
        var inProgressCount = 0
        var completedCount = 0

        for (i in 0 until tasksArray.length()) {
            val taskObject = tasksArray.getJSONObject(i)

            // Пропуск задач пользователя с ID 4
            val userId = taskObject.optInt("user_id", -1)
            if (userId == 4) continue

            // Определяем статус задачи
            val taskStateObject = taskObject.optJSONObject("tasks_state")
            val status = TaskStatus.from(taskStateObject?.optString("name"))
            println("Статус задачи: ${status.status}") // Лог для проверки статуса

            // Заполнение списков задач
            when (status) {
                TaskStatus.PENDING -> if (pendingCount < 3) {
                    addTaskCard(taskObject, taskListPending)
                    pendingCount++
                }
                TaskStatus.IN_PROGRESS -> if (inProgressCount < 3) {
                    addTaskCard(taskObject, taskListInProgress)
                    inProgressCount++
                }
                TaskStatus.COMPLETED -> if (completedCount < 3) {
                    addTaskCard(taskObject, taskListCompleted)
                    completedCount++
                }
            }
        }
    }

    private fun addTaskCard(taskObject: JSONObject, taskListContainer: LinearLayout) {
        val taskCard = LayoutInflater.from(this).inflate(R.layout.task_card, taskListContainer, false)

        val taskTitle: TextView = taskCard.findViewById(R.id.taskTitle)
        val taskDescription: TextView = taskCard.findViewById(R.id.taskDescription)
        val taskAssignee: TextView = taskCard.findViewById(R.id.taskAssignee)

        // Устанавливаем данные задачи
        taskTitle.text = taskObject.optString("title", "Без названия")
        taskDescription.text = taskObject.optString("task_body", "Без описания")

        // Получаем имя исполнителя задачи
        val assignee = taskObject.optJSONObject("users")?.optString("name") ?: "Не указан"
        taskAssignee.text = "Исполнитель: $assignee"

        // Добавляем карточку в список задач
        taskListContainer.addView(taskCard)
    }

    private fun handleError(message: String) {
        runOnUiThread {
            Toast.makeText(this@BoardActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProfilePopup() {
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("authToken")
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
