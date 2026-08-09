package com.example.murycs

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class DetallesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var esFavorito = false

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
        } else {
            boton.text = "Agregar a Favoritos"
            boton.setBackgroundColor(android.graphics.Color.YELLOW)
            boton.setTextColor(android.graphics.Color.BLACK)
        }
    }
}
