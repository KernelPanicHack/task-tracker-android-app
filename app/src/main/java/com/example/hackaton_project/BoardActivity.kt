package com.example.hackaton_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hackaton_project.R

class BoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val taskList: LinearLayout = findViewById(R.id.taskList)
        val addTaskButton: Button = findViewById(R.id.addTask)

        val taskList1: LinearLayout = findViewById(R.id.taskList1)
        val addTaskButton1: Button = findViewById(R.id.addTask1)

        val taskList2: LinearLayout = findViewById(R.id.taskList2)
        val addTaskButton2: Button = findViewById(R.id.addTask2)

        // Слушатель на кнопку для добавления задачи
        addTaskButton.setOnClickListener {
            addNewTask(taskList)
        }
        addTaskButton1.setOnClickListener {
            addNewTask(taskList1)
        }

        addTaskButton2.setOnClickListener {
            addNewTask(taskList2)
        }

    }

    // Метод для добавления новой задачи
    private fun addNewTask(taskList: LinearLayout) {
        // Подгружаем layout карточки задачи
        val taskCard = LayoutInflater.from(this).inflate(R.layout.task_card, taskList, false)

        // Пример настройки данных задачи
        val taskTitle: TextView = taskCard.findViewById(R.id.taskTitle)
        val taskDescription: TextView = taskCard.findViewById(R.id.taskDescription)
        val taskAssignee: TextView = taskCard.findViewById(R.id.taskAssignee)



        // Устанавливаем данные для новой задачи
        taskTitle.text = "Новая задача"
        taskDescription.text = "Описание новой задачи"
        taskAssignee.text = "Исполнитель: Дима2004 Золо"

        // Добавляем карточку задачи в контейнер
        taskList.addView(taskCard)
    }
}