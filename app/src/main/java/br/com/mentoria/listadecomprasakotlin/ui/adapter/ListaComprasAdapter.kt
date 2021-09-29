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

class ListaComprasAdapter(private var listaCompras: ArrayList<Compra>) : RecyclerView.Adapter<ListaComprasAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textQuantidade: TextView = itemView.findViewById(R.id.textQuantidade)
        val textDescricao: TextView = itemView.findViewById(R.id.textNome)
        val textPreco: TextView = itemView.findViewById(R.id.textValor)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_compras_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val compra = listaCompras[position]
        holder.textQuantidade.text = compra.quantidade.toString()
        holder.textDescricao.text = compra.descricao

        val totalPreco = String.format(Locale("pt", "BR"), "R$ %.2f", compra.preco)
        holder.textPreco.text = totalPreco

    }

    override fun getItemCount(): Int {
        return listaCompras.size
    }


}
