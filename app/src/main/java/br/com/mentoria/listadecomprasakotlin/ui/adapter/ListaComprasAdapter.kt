package br.com.mentoria.listadecomprasakotlin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.mentoria.listadecomprasakotlin.R
import br.com.mentoria.listadecomprasakotlin.model.Compra
import java.util.*
import kotlin.collections.ArrayList

class ListaComprasAdapter(val listaCompras: ArrayList<Compra>) : RecyclerView.Adapter<ListaComprasAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_compras_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = listaCompras.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listaCompras[position])
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(compra: Compra) {
            with(compra) {
                itemView.findViewById<TextView>(R.id.textQuantidade).text = quantidade.toString()
                itemView.findViewById<TextView>(R.id.textNome).text = descricao

                val precoFormatado = String.format(Locale("pt", "BR"), "R$ %.2f", preco)
                itemView.findViewById<TextView>(R.id.textValor).text = precoFormatado
            }
        }

    }


}
