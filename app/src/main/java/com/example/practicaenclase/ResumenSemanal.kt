package com.example.practicaenclase

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.practicaenclase.Adapters.ResumenAdapter
import com.example.practicaenclase.Models.Resumen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate
import android.content.Intent
import android.net.Uri

class ResumenSemanal : AppCompatActivity() {

    private lateinit var lvNoticiasUteq: ListView
    private val apiUrl = "https://apiws.uteq.edu.ec/h6RPoSoRaah0Y4Bah28eew/functions/information/entity/3"
    private val token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJfeDF1c2VyZGV2IiwiaWF0IjoxNzgxMjA2NDgwLCJleHAiOjE3ODEyOTI4ODB9.ut9t7jNdM2ubQhp0EZCCytNYR2IQQPmlyoO51V2laGE"
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private fun ignoreSslErrors() {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        try {
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, java.security.SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            Log.e("ResumenSemanal", "SSL bypass error: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ignoreSslErrors()
        enableEdgeToEdge()
        setContentView(R.layout.activity_resumen_semanal)
        
        lvNoticiasUteq = findViewById(R.id.lvNoticiasUteq)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        obtenerDatos()
    }

    private fun obtenerDatos() {
        lifecycleScope.launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    try {
                        val url = URL(apiUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.setRequestProperty("Authorization", "Bearer ${token.trim()}")
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                        connection.connectTimeout = 10000
                        connection.readTimeout = 10000
                        connection.connect()

                        val responseCode = connection.responseCode
                        Log.d("ResumenSemanal", "Response Code: $responseCode")

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            connection.inputStream.bufferedReader().use { it.readText() }
                        } else {
                            val errorMsg = connection.errorStream?.bufferedReader()?.use { it.readText() }
                            Log.e("ResumenSemanal", "Error: $errorMsg")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("ResumenSemanal", "Connection error: ${e.message}")
                        null
                    }
                }

                resultado?.let { jsonString ->
                    Log.d("ResumenSemanal", "JSON: $jsonString")
                    val listaResumen = json.decodeFromString<List<Resumen>>(jsonString)
                    val diezPrimeros = listaResumen.take(10)
                    
                    if (diezPrimeros.isEmpty()) {
                        Toast.makeText(this@ResumenSemanal, "No se encontraron resúmenes", Toast.LENGTH_SHORT).show()
                    } else {
                        val adapter = ResumenAdapter(this@ResumenSemanal, R.layout.item_resumen, diezPrimeros)
                        lvNoticiasUteq.adapter = adapter
                        Toast.makeText(this@ResumenSemanal, "Cargados ${diezPrimeros.size} resúmenes", Toast.LENGTH_SHORT).show()
                    }

                    lvNoticiasUteq.setOnItemClickListener { _, _, position, _ ->
                        val item = diezPrimeros[position]
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.urlvideo1))
                        startActivity(intent)
                    }
                } ?: run {
                    Toast.makeText(this@ResumenSemanal, "Error del servidor. Verifica el Token o la conexión.", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("ResumenSemanal", "Error: ${e.message}")
                Toast.makeText(this@ResumenSemanal, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
