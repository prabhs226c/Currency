package com.prabh.currency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


class ResultActivity : AppCompatActivity() {
    private lateinit var targetCurrency: String
    private var amount: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val baseCurrency = intent.getStringExtra("fromCurrency")
        targetCurrency = intent.getStringExtra("toCurrency")
        amount = intent.getFloatExtra("amount", 0f)

        fetchJson(baseCurrency, targetCurrency)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.linkSbout -> {
                    startActivity(Intent(this,AboutActivity::class.java))
                true
            }
            R.id.linkExit -> {
                finishAffinity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    fun showResult(base: String, date: String, rate: Any?) {
        val exchangeAmount = rate.toString().toFloat() * amount
        findViewById<TextView>(R.id.textViewExchangeTitle).apply {
            text = "1 ${base} = "
        }
        findViewById<TextView>(R.id.textViewExchangeRate).apply {
            text = "${rate} ${targetCurrency} "
        }

        findViewById<TextView>(R.id.textViewAmountExchangeTitle).apply {
            text = "${amount} ${base} = "
        }
        findViewById<TextView>(R.id.textViewAmountExchangeRate).apply {
            text = "${exchangeAmount} ${targetCurrency} "
        }

    }

    private fun fetchJson(base: String, symbol: String) {
        val url = "https://api.exchangeratesapi.io/latest?symbols=${symbol}&base=${base}"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("problem")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val parsedResult = gson.fromJson(body, ParseResult::class.java)

                val rates = parsedResult.rates
                val base = parsedResult.base
                val date = parsedResult.date

                val rate = rates[targetCurrency]
                runOnUiThread {
                    showResult(base, date, rate)
                }
            }

        })

    }

}

class ParseResult(val rates: Map<String, Any>, val base: String, val date: String)
