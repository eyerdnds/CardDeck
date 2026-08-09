package com.example.murycs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.murycs.model.CarResponse
import com.example.murycs.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var adaptadorDeportivos: MakesAdapter
    private lateinit var adaptadorSUV: MakesAdapter
    private lateinit var adaptadorLujo: MakesAdapter

    private val imagenesDeportivos = mapOf(
        "FERRARI" to "https://images.unsplash.com/photo-1592198084033-aade902d1aae?auto=format&fit=crop&q=80&w=1000",
        "LAMBORGHINI" to "https://images.unsplash.com/photo-1544636331-e26879cd4d9b?auto=format&fit=crop&q=80&w=1000",
        "PORSCHE" to "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&q=80&w=1000",
        "BUGATTI" to "https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?auto=format&fit=crop&q=80&w=1000",
        "MCLAREN" to "https://images.unsplash.com/photo-1621135802920-133df287f89c?auto=format&fit=crop&q=80&w=1000",
        "ASTON MARTIN" to "https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?auto=format&fit=crop&q=80&w=1000",
        "MASERATI" to "https://images.unsplash.com/photo-1596464522432-613ca92a6c11?auto=format&fit=crop&q=80&w=1000",
        "LOTUS" to "https://images.unsplash.com/photo-1616422285623-13ff0162193c?auto=format&fit=crop&q=80&w=1000"
    )

    private val imagenesSUV = mapOf(
        "JEEP" to "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&q=80&w=1000",
        "LAND ROVER" to "https://images.unsplash.com/photo-1506015391300-4802dc7bbca2?auto=format&fit=crop&q=80&w=1000",
        "HUMMER" to "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&q=80&w=1000",
        "TOYOTA" to "https://images.unsplash.com/photo-1594731802114-03994285b78f?auto=format&fit=crop&q=80&w=1000",
        "FORD" to "https://images.unsplash.com/photo-1551816230-ef5deaed4a28?auto=format&fit=crop&q=80&w=1000"
    )

    private val imagenesLujo = mapOf(
        "ROLLS ROYCE" to "https://images.unsplash.com/photo-1631214524020-7e18db9a8f92?auto=format&fit=crop&q=80&w=1000",
        "BENTLEY" to "https://images.unsplash.com/photo-1562619371-b67725b6fde2?auto=format&fit=crop&q=80&w=1000",
        "MERCEDES-BENZ" to "https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?auto=format&fit=crop&q=80&w=1000",
        "BMW" to "https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&q=80&w=1000",
        "AUDI" to "https://images.unsplash.com/photo-1542282088-fe8426682b8f?auto=format&fit=crop&q=80&w=1000"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarRecyclerViews(view)
        obtenerTodosLosDatos(view)
    }

    private fun configurarRecyclerViews(view: View) {
        val rvDeportivos = view.findViewById<RecyclerView>(R.id.rvSports)
        val rvSUV = view.findViewById<RecyclerView>(R.id.rvSUV)
        val rvLujo = view.findViewById<RecyclerView>(R.id.rvLuxury)

        adaptadorDeportivos = MakesAdapter(emptyList(), "Deportivos")
        adaptadorSUV = MakesAdapter(emptyList(), "SUV")
        adaptadorLujo = MakesAdapter(emptyList(), "Lujo")

        rvDeportivos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvDeportivos.adapter = adaptadorDeportivos

        rvSUV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSUV.adapter = adaptadorSUV

        rvLujo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvLujo.adapter = adaptadorLujo
    }

    private fun obtenerTodosLosDatos(view: View) {
        obtenerDatosCategoria(
            imagenesDeportivos,
            adaptadorDeportivos,
            view.findViewById(R.id.progressBarSports),
            view.findViewById(R.id.tvEstadoSports)
        )
        obtenerDatosCategoria(
            imagenesSUV,
            adaptadorSUV,
            view.findViewById(R.id.progressBarSUV),
            view.findViewById(R.id.tvEstadoSUV)
        )
        obtenerDatosCategoria(
            imagenesLujo,
            adaptadorLujo,
            view.findViewById(R.id.progressBarLuxury),
            view.findViewById(R.id.tvEstadoLuxury)
        )
    }

    private fun matchCategory(mapa: Map<String, String>): String {
        return when (mapa) {
            imagenesDeportivos -> "Deportivos"
            imagenesSUV -> "SUV"
            imagenesLujo -> "Lujo"
            else -> "Otros"
        }
    }

    private fun obtenerDatosCategoria(
        mapaImagenes: Map<String, String>,
        adaptador: MakesAdapter,
        barraProgreso: ProgressBar,
        textoEstado: TextView
    ) {
        barraProgreso.visibility = View.VISIBLE
        textoEstado.text = "Cargando..."

        RetrofitClient.instance.getAllMakes().enqueue(object : Callback<CarResponse> {
            override fun onResponse(call: Call<CarResponse>, response: Response<CarResponse>) {
                if (!isAdded) return
                barraProgreso.visibility = View.GONE
                if (response.isSuccessful) {
                    textoEstado.text = "Resultados"
                    val todosLosModelos = response.body()?.results ?: emptyList()

                    val autosFiltrados = todosLosModelos.filter { mapaImagenes.containsKey(it.name.uppercase()) }
                        .map { auto ->
                            auto.copy(
                                imageUrl = mapaImagenes[auto.name.uppercase()],
                                category = matchCategory(mapaImagenes)
                            )
                        }

                    adaptador.actualizarDatos(autosFiltrados)
                } else {
                    textoEstado.text = "Error"
                    Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CarResponse>, t: Throwable) {
                if (!isAdded) return
                barraProgreso.visibility = View.GONE
                textoEstado.text = "Error de red"
                Toast.makeText(requireContext(), "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}