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
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class BoardActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var authToken: String? = null
    private var userId: String? = null

    // Объявляем переменные для профиля как свойства класса
    private var fullName: String = ""
    private var position: String = ""
    private var registrationDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        authToken = intent.getStringExtra("TOKEN")
        userId = intent.getStringExtra("USER_ID")

        // Загружаем профиль пользователя по userId
        userId?.let { fetchUserProfile(it) }

        val profileButton: ImageButton = findViewById(R.id.Profile)
        profileButton.setOnClickListener {
            showProfilePopup()
        }
    }

    private fun fetchUserProfile(userId: String) {
        val url = "https://356d-188-162-172-157.ngrok-free.app/api/users/$userId" // URL для запроса

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    val jsonResponse = JSONObject(responseData)

                    // Получаем данные профиля
                    fullName = "${jsonResponse.optString("surname", "")} " +
                            "${jsonResponse.optString("name", "")} " +
                            "${jsonResponse.optString("patronymic", "")}"
                    position = jsonResponse.optString("specialization", "Не указано")
                    registrationDate = jsonResponse.optString("created_at", "Не указана")

                    // Обновляем UI
                    runOnUiThread {
                        showProfilePopup() // Вызов без параметров
                    }
                } else {
                    // Обработка неудачного запроса
                }
            }
        })
    }

    private fun showProfilePopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_menu, null)

        val profileName = dialogView.findViewById<TextView>(R.id.profileName)
        val profilePosition = dialogView.findViewById<TextView>(R.id.profilePosition)
        val profileRegistrationDate = dialogView.findViewById<TextView>(R.id.profileRegistrationDate)
        val logoutButton = dialogView.findViewById<Button>(R.id.button)

        // Устанавливаем данные профиля
        profileName.text = fullName
        profilePosition.text = "Должность: $position"
        profileRegistrationDate.text = "Дата регистрации: $registrationDate"

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
