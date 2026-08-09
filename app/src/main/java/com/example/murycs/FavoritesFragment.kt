package com.example.murycs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: MakesAdapter
    private lateinit var rvFavorites: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var pbFavorites: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        
        dbHelper = DatabaseHelper(requireContext())
        
        rvFavorites = view.findViewById(R.id.rvFavorites)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        pbFavorites = view.findViewById(R.id.pbFavorites)
        
        // Usamos GridLayoutManager con 2 columnas para que se vea bien la lista
        rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = MakesAdapter(emptyList(), "Favoritos")
        rvFavorites.adapter = adapter
        
        return view
    }

    override fun onResume() {
        super.onResume()
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        pbFavorites.visibility = View.VISIBLE
        val listaFavoritos = dbHelper.obtenerFavoritos()
        pbFavorites.visibility = View.GONE
        
        if (listaFavoritos.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvFavorites.visibility = View.VISIBLE
            adapter.actualizarDatos(listaFavoritos)
        }
    }
}