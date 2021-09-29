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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mentoria.listadecomprasakotlin.R
import br.com.mentoria.listadecomprasakotlin.databinding.ActivityListaComprasBinding
import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import br.com.mentoria.listadecomprasakotlin.model.Compra
import br.com.mentoria.listadecomprasakotlin.ui.adapter.ListaComprasAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class ListaComprasActivity : AppCompatActivity() {

    lateinit var binding: ActivityListaComprasBinding
    lateinit var valueEventListener: ValueEventListener
    lateinit var adapter: ListaComprasAdapter
    var autenticacao: FirebaseAuth = Firebase.auth
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var listaCompras: ArrayList<Compra> = ArrayList()
    var valorTotal: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaComprasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLista)
        configuraRecyclerView(binding)

    }

    override fun onStart() {
        super.onStart()
        carregarListaCompras()
    }

    private fun configuraRecyclerView(binding: ActivityListaComprasBinding) {

        val recyclerLista = binding.contentMain.recyclerLista
        recyclerLista.layoutManager = LinearLayoutManager(this)
        recyclerLista.setHasFixedSize(true)
        recyclerLista.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        adapter = ListaComprasAdapter(listaCompras)
        recyclerLista.adapter = adapter

    }

    private fun carregarListaCompras() {
        val idUsuario = Base64Custom.codificar(autenticacao.currentUser?.email.toString())
        val databaseRef = database.child("lista").child(idUsuario)
        valueEventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                listaCompras.clear()
                for (dados in snapshot.children) {
                    val compra = dados.getValue(Compra::class.java)!!
                    listaCompras.add(compra)
                    valorTotal = compra.total
                }
                binding.contentMain.textoTotalCompra.text =
                    String.format(Locale("pt", "BR"),"R$ %.2f", valorTotal)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        databaseRef.addValueEventListener(valueEventListener)
    }

    fun abrirTelaAdicionarItem(view: View) {
        val intent = Intent(this, AdicionarItemActivity::class.java)
        startActivity(intent)
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

    private fun deslogar() {
        autenticacao.signOut()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        database.removeEventListener(valueEventListener)
    }

}