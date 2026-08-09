package com.example.murycs

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.murycs.model.Car

class MakesAdapter(
    private var autos: List<Car>,
    private val categoria: String
) : RecyclerView.Adapter<MakesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvId: TextView = view.findViewById(R.id.tvId)
        val img: ImageView = view.findViewById(R.id.img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val auto = autos[position]
        holder.tvNombre.text = auto.name
        holder.tvId.text = "ID: ${auto.id}"

        Glide.with(holder.itemView.context)
            .load(auto.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetallesActivity::class.java).apply {
                putExtra("car_id", auto.id)
                putExtra("car_name", auto.name)
                putExtra("car_image", auto.imageUrl)
                putExtra("car_category", auto.category ?: categoria)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = autos.size

    fun actualizarDatos(nuevosAutos: List<Car>) {
        autos = nuevosAutos
        notifyDataSetChanged()
    }
}
