package com.example.murycs

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.murycs.HomeActivity
import com.example.murycs.RegisterActivity
import com.example.murycs.DatabaseHelper
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvRegistrar: TextView
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager : CredentialManager

    private lateinit var btnGoogle : ImageButton
    private lateinit var btnTwitter : ImageButton
    private lateinit var btnFacebook : LoginButton
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvRegistrar = findViewById(R.id.tvRegistrar)
        etCorreo = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        dbHelper = DatabaseHelper(this)

        btnGoogle = findViewById(R.id.btnGoogle)
        btnTwitter = findViewById(R.id.btnTwitter)
        btnFacebook = findViewById(R.id.btnFacebook)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        // Configurar Facebook Login
        callbackManager = CallbackManager.Factory.create()
        btnFacebook.setReadPermissions("email", "public_profile")
        btnFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                autenticarFirebaseFacebook(result.accessToken.token)
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        tvRegistrar.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            iniciarSesion()
        }

        btnGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        btnTwitter.setOnClickListener {
            iniciarSesionTwitter()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Metodo para iniciar sesion desde el MainActivity
    private fun iniciarSesion() {

        val email = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        // Validar si los campos no estan vacios

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val inicioExitoso = dbHelper.iniciarSesion(correo = email, password = pass)

        // Evaluar si el inicio fue exitoso
        if (inicioExitoso){
            Toast.makeText(this,"Bienvenido",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            Toast.makeText(this,"Datos incorrectos",Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarSesionTwitter() {
        val provider = OAuthProvider.newBuilder("twitter.com")

        // 1. Revisar si hay un resultado pendiente
        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener { authResult ->
                    // El usuario ya estaba en el flujo
                    Toast.makeText(this, "Bienvenido ${authResult.user?.displayName}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // 2. No hay resultado pendiente, iniciar el flujo normal
            auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener { authResult ->
                    Toast.makeText(this, "Bienvenido ${authResult.user?.displayName}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al iniciar sesión con Twitter: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // 3. METODO PARA AUTENTICAR EN FIREBSAE
    private fun autenticarFirebase(idToken: String){

        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) {

                inicio ->

            // Validar si el inicio fue exitoso
            if(inicio.isSuccessful){
                val usuario = auth.currentUser

                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

                finish()

            } else {
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun autenticarFirebaseFacebook(token: String) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val usuario = auth.currentUser
                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al iniciar sesión con Facebook", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 4. METODO PARA PROCESAR LA CREDENCIAL
    private fun procesarCredencial(credential: Credential){

        if(credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){

            try {

                val credencialGoogle = GoogleIdTokenCredential.createFrom(credential.data)

                autenticarFirebase(credencialGoogle.idToken)

            }catch (e : GoogleIdTokenParsingException){

                Toast.makeText(this, "Error: ${e}", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // 5. METODO PARA INICIAR SESIÓN
    private fun iniciarSesionGoogle(){

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        lifecycleScope.launch {

            try {

                val resultado = credentialManager.getCredential(context = this@MainActivity, request = solicitud)

                procesarCredencial(resultado.credential)

            }catch (e : GetCredentialException){
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

}