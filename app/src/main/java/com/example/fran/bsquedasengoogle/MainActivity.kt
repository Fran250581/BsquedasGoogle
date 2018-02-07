package com.example.fran.bsquedasengoogle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.widget.TextView
import android.widget.EditText
import android.os.StrictMode
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {

    private var entrada: EditText? = null
    private var salida: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        entrada = findViewById<View>(R.id.EditText01) as EditText?
        salida = findViewById<View>(R.id.TextView01) as TextView?
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitNetwork().build())
    }

    fun buscar(view: View) {
        try {
            val palabras = entrada!!.getText().toString()
            val resultado = resultadosGoogle(palabras)
            salida!!.append(palabras + "--" + resultado + "\n")
        }
        catch (e: Exception) {
            salida!!.append("Error al conectar\n")
            Log.e("HTTP", e.message, e)
        }
    }

    @Throws(Exception::class)
    private fun resultadosGoogle(palabras: String): String {
        var pagina = ""
        var devuelve = ""
        val url = URL("https://www.google.es/search?hl=es&q=\""
                + URLEncoder.encode(palabras, "UTF-8") + "\"")
        val conexion = url.openConnection() as HttpURLConnection
        conexion.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)")
        if (conexion.getResponseCode() === HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(conexion.getInputStream()))
            var linea = reader.readLine()
            while (linea != null) {
                pagina += linea
                linea = reader.readLine()
            }
            reader.close()
            val ini = pagina.indexOf("Aproximadamente")
            if (ini != -1) {
                val fin = pagina.indexOf(" ", ini + 16)
                devuelve = pagina.substring(ini + 16, fin)
            }
            else {
                devuelve = "no encontrado"
            }
        }
        else {
            salida!!.append("ERROR: " + conexion.getResponseMessage() + "\n")
        }
        conexion.disconnect()
        return devuelve
    }

}
