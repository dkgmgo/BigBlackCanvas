package com.example.bigblackcanvas.utils

import com.example.bigblackcanvas.models.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OutUtils {

    companion object{
        suspend fun getRandomQuote(): Quote{
            return getQuote("random")
        }

        suspend fun getTodayQuote(): Quote {
            return getQuote("today")
        }
        suspend fun getQuote(endpoint: String): Quote{
            return withContext(Dispatchers.IO){
                val fullUrl = "https://zenquotes.io/api/"+endpoint
                var sortie = Quote("Failed to get Quote", "Kotlin", "")

                try{
                    val url = URL(fullUrl)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"

                    if(conn.responseCode == HttpURLConnection.HTTP_OK){
                        val iS = conn.inputStream
                        val br = BufferedReader(InputStreamReader(iS))
                        val body = StringBuilder()
                        var line: String?

                        while (br.readLine().also { line = it } != null) {
                            body.append(line)
                        }

                        br.close()
                        iS.close()

                        val jsonObj = JSONArray(body.toString()).getJSONObject(0)
                        val text = jsonObj.getString("q")
                        val author = jsonObj.getString("a")
                        val html = jsonObj.getString("h")

                        sortie = Quote(text, author, html)
                    }

                    conn.disconnect()
                }catch (e: Exception){
                    println("Error: $e")
                }
                sortie
            }
        }
    }
}