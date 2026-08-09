package com.example.murycs

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper (
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,
) {

    companion object {
        private const val DATABASE_NAME = "carddeck.db"
        private const val DATABASE_VERSION = 3 // Incrementado para incluir favoritos
        
        // Tabla Usuarios
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_USUARIO_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

        // Tabla Favoritos
        private const val TABLE_FAVORITOS = "favoritos_albumes"
        private const val COL_FAVORITO_ID = "id"
        private const val COL_ALBUM_ID = "album_id"
        private const val COL_NOMBRE_ALBUM = "nombre_album"
        private const val COL_ARTISTA = "artista"
        private const val COL_GENERO = "genero"
        private const val COL_FECHA = "fecha"
        private const val COL_IMAGEN = "imagen"

        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COL_NOMBRE TEXT NOT NULL, 
                $COL_CORREO TEXT NOT NULL UNIQUE, 
                $COL_PASSWORD TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS (
                $COL_FAVORITO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ALBUM_ID INTEGER UNIQUE NOT NULL,
                $COL_NOMBRE_ALBUM TEXT NOT NULL,
                $COL_ARTISTA TEXT,
                $COL_GENERO TEXT,
                $COL_FECHA TEXT,
                $COL_IMAGEN TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_FAVORITOS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITOS")
        onCreate(db)
    }

    fun registrarUsuario(nombre: String, correo: String, password: String) : Boolean {
        val db = writableDatabase
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }
        val resultado = db.insert(TABLE_USUARIOS, null, datos)
        db.close()
        return resultado != -1L
    }

    fun iniciarSesion(correo: String, password: String) : Boolean {
        val db = readableDatabase
        val querySql = "SELECT * FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?"
        val cursor = db.rawQuery(querySql, arrayOf(correo, password))
        val usuarioEncontrado = cursor.count > 0
        cursor.close()
        db.close()
        return usuarioEncontrado
    }

    // CRUD para Favoritos
    fun agregarAFavoritos(idAuto: Int, nombre: String, categoria: String, imagen: String): Boolean {
        val db = writableDatabase
        val datos = ContentValues().apply {
            put(COL_ALBUM_ID, idAuto)
            put(COL_NOMBRE_ALBUM, nombre)
            put(COL_ARTISTA, categoria)
            put(COL_IMAGEN, imagen)
        }
        val resultado = db.insert(TABLE_FAVORITOS, null, datos)
        db.close()
        return resultado != -1L
    }

    fun eliminarDeFavoritos(idAuto: Int): Boolean {
        val db = writableDatabase
        val resultado = db.delete(TABLE_FAVORITOS, "$COL_ALBUM_ID = ?", arrayOf(idAuto.toString()))
        db.close()
        return resultado > 0
    }

    fun esFavorito(idAuto: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FAVORITOS WHERE $COL_ALBUM_ID = ?", arrayOf(idAuto.toString()))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun obtenerFavoritos(): List<com.example.murycs.model.Car> {
        val lista = mutableListOf<com.example.murycs.model.Car>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FAVORITOS", null)
        
        if (cursor.moveToFirst()) {
            do {
                val car = com.example.murycs.model.Car(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ALBUM_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_ALBUM)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGEN)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COL_ARTISTA))
                )
                lista.add(car)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}
