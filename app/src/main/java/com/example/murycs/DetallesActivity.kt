package com.example.murycs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class DetallesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var esFavorito = false
    private val CHANNEL_ID = "favoritos_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalles)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        createNotificationChannel()

        val id = intent.getIntExtra("car_id", -1)
        val nombre = intent.getStringExtra("car_name") ?: ""
        val imagen = intent.getStringExtra("car_image") ?: ""
        val categoria = intent.getStringExtra("car_category") ?: ""

        val ivImagen = findViewById<ImageView>(R.id.imgDetalle)
        val tvNombre = findViewById<TextView>(R.id.tvNombreDetalle)
        val tvId = findViewById<TextView>(R.id.tvIdDetalle)
        val tvCategoria = findViewById<TextView>(R.id.tvCategoriaDetalle)
        val btnFavorito = findViewById<Button>(R.id.btnFavorito)

        tvNombre.text = nombre
        tvId.text = "ID: $id"
        tvCategoria.text = "Categoría: $categoria"

        Glide.with(this)
            .load(imagen)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(ivImagen)

        esFavorito = dbHelper.esFavorito(id)
        actualizarBotonFavorito(btnFavorito)

        btnFavorito.setOnClickListener {
            if (esFavorito) {
                if (dbHelper.eliminarDeFavoritos(id)) {
                    esFavorito = false
                    Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (dbHelper.agregarAFavoritos(id, nombre, categoria, imagen)) {
                    esFavorito = true
                    showNotification(nombre)
                    Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                }
            }
            actualizarBotonFavorito(btnFavorito)
        }
    }

    private fun actualizarBotonFavorito(boton: Button) {
        if (esFavorito) {
            boton.text = "Quitar de Favoritos"
            boton.setBackgroundColor(android.graphics.Color.RED)
            boton.setTextColor(android.graphics.Color.WHITE)
            boton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_on, 0, 0, 0)
        } else {
            boton.text = "Agregar a Favoritos"
            boton.setBackgroundColor(android.graphics.Color.YELLOW)
            boton.setTextColor(android.graphics.Color.BLACK)
            boton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_off, 0, 0, 0)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de Favoritos"
            val descriptionText = "Canal para avisar cuando se guarda un favorito"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(nombreAlbum: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.btn_star_big_on)
            .setContentTitle("¡Nuevo favorito!")
            .setContentText("Has guardado $nombreAlbum en tus favoritos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}
