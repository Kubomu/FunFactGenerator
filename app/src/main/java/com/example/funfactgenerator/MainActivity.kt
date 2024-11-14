package com.cruise.FunFactApp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import api.FactApiService
import com.example.funfactgenerator.models.FactResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var factTextView: TextView
    private lateinit var generateButton: Button
    private lateinit var previousButton: Button
    private lateinit var factApiService: FactApiService

    // List to store previously generated facts
    private val factsList = mutableListOf<String>()
    private var currentFactIndex = -1 // Index for the currently displayed fact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        factTextView = findViewById(R.id.factTextView)
        generateButton = findViewById(R.id.generateButton)
        previousButton = findViewById(R.id.previousButton)

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

        // Set button click listener to show the previous fact dialog
        previousButton.setOnClickListener {
            showPreviousFactsDialog()
        }
    }

    private fun fetchRandomFact() {
        val call = factApiService.getRandomFact()

        call.enqueue(object : Callback<FactResponse> {
            override fun onResponse(call: Call<FactResponse>, response: Response<FactResponse>) {
                if (response.isSuccessful) {
                    val fact = response.body()?.fact
                    if (fact != null) {
                        factTextView.text = fact

                        // Add the new fact to the list and update the index
                        factsList.add(fact)
                        currentFactIndex = factsList.size - 1
                    } else {
                        factTextView.text = "No fact found."
                    }
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

    private fun showPreviousFactsDialog() {
        if (factsList.isNotEmpty()) {
            // Create a string to display all previous facts, with each on a new line
            val previousFacts = factsList.joinToString(separator = "\n\n") { fact -> "- $fact" }

            // Show the dialog with the title "#PreviousFacts"
            AlertDialog.Builder(this)
                .setTitle("#PreviousFacts")
                .setMessage(previousFacts)
                .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                .show()
        } else {
            // Show a message if no previous facts are available
            Toast.makeText(this, "No previous facts available", Toast.LENGTH_SHORT).show()
        }
    }
}
