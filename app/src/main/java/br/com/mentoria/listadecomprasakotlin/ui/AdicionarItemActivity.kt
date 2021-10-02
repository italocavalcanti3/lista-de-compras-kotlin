package br.com.mentoria.listadecomprasakotlin.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import br.com.mentoria.listadecomprasakotlin.R
import br.com.mentoria.listadecomprasakotlin.databinding.ActivityAdicionarItemBinding
import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import br.com.mentoria.listadecomprasakotlin.model.Compra
import br.com.mentoria.listadecomprasakotlin.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*

class AdicionarItemActivity : AppCompatActivity() {

    lateinit var textQuantidade: EditText
    lateinit var textDescricao: EditText
    lateinit var textPreco: EditText
    lateinit var usuario: Usuario
    lateinit var compra: Compra
    lateinit var compraRecuperada: Compra
    lateinit var listaVEL: ValueEventListener
    private var autenticacao: FirebaseAuth = Firebase.auth
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var idCompra: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Adicionar compra"

        geradorDeId()

        textDescricao = binding.textNome
        textQuantidade = binding.textQuantidade
        textPreco = binding.textValor

        if (intent.hasExtra("compra")) {
            compraRecuperada = intent.extras?.get("compra") as Compra
            Toast.makeText(this, compraRecuperada.idCompra.toString(), Toast.LENGTH_SHORT).show()
            textDescricao.setText(compraRecuperada.descricao)
            textQuantidade.setText(compraRecuperada.quantidade.toString())
            textPreco.setText(compraRecuperada.preco.toString())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.adicionar_item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId

        when (itemId) {
            R.id.salvar -> {
                val validaCampos = validarCampos(textDescricao, textQuantidade, textPreco)
                if (validaCampos) {

                    val quantidade: Int = textQuantidade.text.toString().toInt()
                    val descricao: String = textDescricao.text.toString()
                    val preco: Double = textPreco.text.toString().toDouble()

                    if (this::compraRecuperada.isInitialized) { // ATUALIZA

                        compraRecuperada.quantidade = quantidade
                        compraRecuperada.descricao = descricao
                        compraRecuperada.preco = preco
                        compraRecuperada.total = preco * quantidade
                        compraRecuperada.salva(autenticacao.currentUser?.email.toString())

                    } else { // SALVA
                        compra = Compra(idCompra, quantidade, descricao, preco)
                        compra.salva(autenticacao.currentUser?.email.toString())
                    }
                    finish()
                } else {
                    Log.i("ADICIONAR_ITEM", "Erro ao salvar item")
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun validarCampos(
        textDescricao: EditText,
        textQuantidade: EditText,
        textPreco: EditText
    ): Boolean {
        if (textDescricao.text.isNotEmpty()) {
            if (textQuantidade.text.isNotEmpty()) {
                if (textPreco.text.isNotEmpty()) {
                    return true
                } else {
                    Toast.makeText(this, "Preencha o preço", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha a quantidade", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Preencha a descrição", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun geradorDeId() {
        val idUsuario = Base64Custom.codificar(autenticacao.currentUser?.email.toString())
        val databaseRef = database.child("lista").child(idUsuario)
        idCompra = databaseRef.push().key.toString()
    }

}
