package com.example.hackaton_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private var authToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        authToken = sharedPreferences.getString("authToken", null)

        if (authToken != null) {
            navigateToBoardActivity(authToken!!)
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton: Button = findViewById(R.id.registrButton)
        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.logintxt).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()
            login(email, password)
        }

        val registerButton: Button = findViewById(R.id.buttonIf)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegistrActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        val url = "https://6fd4-188-162-141-162.ngrok-free.app/api/login"
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                println("Ответ от сервера: $responseData")

                if (response.isSuccessful && responseData != null) {
                    val jsonResponse = JSONObject(responseData)
                    authToken = jsonResponse.optString("token")

                    if (!authToken.isNullOrEmpty()) {
                        saveAuthToken(authToken!!)
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Успешный вход", Toast.LENGTH_SHORT).show()
                            navigateToBoardActivity(authToken!!)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Ошибка входа: Неверные данные", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ошибка входа: Неверные данные", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun saveAuthToken(token: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("authToken", token)
            apply()
        }
    }

    private fun navigateToBoardActivity(token: String) {
        val intent = Intent(this@MainActivity, BoardActivity::class.java)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
        finish()
    }
}
