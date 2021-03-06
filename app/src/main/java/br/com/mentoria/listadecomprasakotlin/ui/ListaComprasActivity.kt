package br.com.mentoria.listadecomprasakotlin.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mentoria.listadecomprasakotlin.R
import br.com.mentoria.listadecomprasakotlin.config.RecyclerItemClickListener
import br.com.mentoria.listadecomprasakotlin.databinding.ActivityListaComprasBinding
import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import br.com.mentoria.listadecomprasakotlin.model.Compra
import br.com.mentoria.listadecomprasakotlin.ui.adapter.ListaComprasAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class ListaComprasActivity : AppCompatActivity() {

    lateinit var listaVEL: ValueEventListener
    lateinit var saldoVEL: ValueEventListener
    lateinit var adapter: ListaComprasAdapter
    var autenticacao: FirebaseAuth = Firebase.auth
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var listaCompras: ArrayList<Compra> = ArrayList()
    var saldoTotal = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityListaComprasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLista)
        configuraRecyclerView(binding)

        //Configura Swipe
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelper())
        itemTouchHelper.attachToRecyclerView(binding.contentMain.recyclerLista)

    }

    override fun onStart() {
        super.onStart()
        carregarSaldoTotal()
        carregarListaCompras()
    }

    override fun onResume() {
        super.onResume()
        carregarSaldoTotal()
    }

    override fun onStop() {
        super.onStop()
        database.removeEventListener(listaVEL)
        database.removeEventListener(saldoVEL)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        menu?.apply {
            for (i in 0 until this.size()) {
                val item = menu.getItem(i)
                val s = SpannableString(item.title)
                s.setSpan(ForegroundColorSpan(Color.BLACK), 0, s.length, 0)
                item.title = s
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId

        when (itemId) {
            R.id.botaoDeslogar -> deslogar()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarListaCompras() {
        val idUsuario = Base64Custom.codificar(autenticacao.currentUser?.email.toString())
        val databaseRef = database.child("lista").child(idUsuario)
        listaVEL = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                listaCompras.clear()
                for (dados in snapshot.children) {
                    val compra = dados.getValue(Compra::class.java)!!
                    listaCompras.add(compra)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        databaseRef.addValueEventListener(listaVEL)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarSaldoTotal() {

        val idUsuario = Base64Custom.codificar(autenticacao.currentUser?.email.toString())
        val databaseRef = database.child("lista").child(idUsuario)
        saldoVEL = object : ValueEventListener {
            var saldoTotal = 0.00
            override fun onDataChange(snapshot: DataSnapshot) {
                for (compra in snapshot.children) {
                    val total = compra.getValue(Compra::class.java)?.total
                    if (total != null) {
                        saldoTotal += total
                    }
                }
                val saldoFormatado = String.format(Locale("pt", "BR"), "R$ %.2f", saldoTotal)
                findViewById<TextView>(R.id.textoTotalCompra).text = saldoFormatado
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("ERRO", error.message)
            }

        }
        databaseRef.addValueEventListener(saldoVEL)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun limparLista(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar a????o")
        builder.setMessage("Tem certeza que deseja limpar a lista?")
        builder.setNegativeButton("N??o") { d, i -> }
        builder.setPositiveButton("Sim") { dialog, wich ->
            val idUsuario = Base64Custom.codificar(autenticacao.currentUser?.email.toString())
            val databaseRef = database.child("lista").child(idUsuario)
            databaseRef.removeValue()
            listaCompras.clear()
            saldoTotal = 0.00
            adapter.notifyDataSetChanged()
            carregarSaldoTotal()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun excluirMovimentacao(compra: Compra) {
        val idUsuario = Base64Custom.codificar(autenticacao.currentUser?.email.toString())
        val databaseRef = database.child("lista").child(idUsuario).child(compra.idCompra)
        databaseRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Item removido", Toast.LENGTH_SHORT).show()
                carregarSaldoTotal()
            } else {
                Toast.makeText(this, "Erro ao excluir item", Toast.LENGTH_SHORT).show()
                task.exception?.printStackTrace()
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun configuraRecyclerView(binding: ActivityListaComprasBinding) {
        val recyclerLista = binding.contentMain.recyclerLista
        recyclerLista.layoutManager = LinearLayoutManager(this)
        recyclerLista.setHasFixedSize(true)
        recyclerLista.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        adapter = ListaComprasAdapter(listaCompras)
        recyclerLista.adapter = adapter

        recyclerLista.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerLista,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        abrirActivityAlteraItem(listaCompras[position])
                    }

                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        mostraAlertaExcluirItem(listaCompras[position])
                    }

                }
            )
        )
    }

    //    Implementa????o do RecyclerItemClickListener
    inner class ItemTouchHelper : Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags: Int = ACTION_STATE_IDLE
            val swipeFlags: Int = END or START
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            mostraAlertaExcluirItem(listaCompras[viewHolder.adapterPosition])
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun mostraAlertaExcluirItem(compra: Compra) {
        val alert = AlertDialog.Builder(this@ListaComprasActivity)
        alert.setTitle("Confirmar a????o")
        alert.setMessage("Tem certeza que deseja limpar a lista?")
        alert.setCancelable(false)
        alert.setNegativeButton("N??o") { d, i -> }
        alert.setPositiveButton("Sim") { d, i -> excluirMovimentacao(compra) }
        alert.create()
        alert.show()
        adapter.notifyDataSetChanged();
    }

    private fun abrirActivityAlteraItem(compra: Compra) {
        val intent = Intent(this, AdicionarItemActivity::class.java)
        intent.putExtra("compra", compra as Serializable)
        startActivity(intent)
    }

    fun abrirTelaAdicionarItem(view: View) {
        val intent = Intent(this, AdicionarItemActivity::class.java)
        startActivity(intent)
    }

    private fun deslogar() {
        autenticacao.signOut()
        finish()
    }

}