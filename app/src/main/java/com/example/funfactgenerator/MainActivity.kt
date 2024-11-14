package com.cruise.FunFactApp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import api.FactApiService
import com.example.funfactgenerator.models.FactResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Switch


class MainActivity : AppCompatActivity() {

    private lateinit var factTextView: TextView
    private lateinit var generateButton: Button
    private lateinit var factApiService: FactApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        factTextView = findViewById(R.id.factTextView)
        generateButton = findViewById(R.id.generateButton)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://uselessfacts.jsph.pl/") // Base URL of the API
            .addConverterFactory(GsonConverterFactory.create()) // To convert JSON to Kotlin object
            .build()

        factApiService = retrofit.create(FactApiService::class.java)

        // Set button click listener to fetch a new fact
        generateButton.setOnClickListener {
            fetchRandomFact()
        }
    }



    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Retrieve theme preference
            val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
            val isDarkMode = sharedPref.getBoolean("DARK_MODE", false)

            // Apply theme based on preference
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            setContentView(R.layout.activity_main)


        }
    }



    private fun fetchRandomFact() {
        val call = factApiService.getRandomFact()

        call.enqueue(object : Callback<FactResponse> {
            override fun onResponse(call: Call<FactResponse>, response: Response<FactResponse>) {
                if (response.isSuccessful) {
                    val fact = response.body()?.fact
                    factTextView.text = fact ?: "No fact found."
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load fact: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FactResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Error fetching fact", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
