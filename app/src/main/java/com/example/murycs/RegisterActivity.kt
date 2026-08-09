package com.example.murycs

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnRegisto: Button
    private lateinit var tvInicio: TextView

    //Manejar atributo para el objeto de la clase DatabaseHelper
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnRegisto = findViewById(R.id.btnRegistrar)
        tvInicio = findViewById(R.id.tvInicio)

        // Objeto de la clase DatabaseHelper
        dbHelper = DatabaseHelper(this)

        btnRegisto.setOnClickListener {
            registrarUsuario()
        }

        tvInicio.setOnClickListener {
            finish()
        }

    }

    private fun registrarUsuario(){

        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validar que los datos no estén en blanco
        if(nombre.isEmpty() || email.isEmpty() || password.isEmpty()){

            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return

        }
        val resultado = dbHelper.registrarUsuario(nombre,correo = email, password)

        //validar el registro fue exitoso
        if (resultado) {
            Toast.makeText(this, "Registro exitoso",Toast.LENGTH_SHORT).show()
            finish()

        } else {

            Toast.makeText(this, "Error en el registro",Toast.LENGTH_SHORT).show()
        }

    }

}