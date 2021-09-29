package br.com.mentoria.listadecomprasakotlin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.mentoria.listadecomprasakotlin.R
import br.com.mentoria.listadecomprasakotlin.databinding.ActivityAdicionarItemBinding
import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import br.com.mentoria.listadecomprasakotlin.model.Compra
import br.com.mentoria.listadecomprasakotlin.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class AdicionarItemActivity : AppCompatActivity() {

    lateinit var textQuantidade: Editable
    lateinit var textDescricao: Editable
    lateinit var textPreco: Editable
    lateinit var usuario: Usuario
    lateinit var compra: Compra
    private var autenticacao: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Adicionar compra"

        textDescricao = binding.textNome.text
        textQuantidade = binding.textQuantidade.text
        textPreco = binding.textValor.text

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
                    recuperaCamposSalvaCompraFirebase(textQuantidade, textDescricao, textPreco)
                    finish()
                } else {
                    Log.i("ADICIONAR_ITEM", "Erro ao salvar item")
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun recuperaCamposSalvaCompraFirebase(
        textQuantidade: Editable,
        textDescricao: Editable,
        textPreco: Editable
    ) {
        val quantidade: Int = textQuantidade.toString().toInt()
        val descricao: String = textDescricao.toString()
        val preco: Double = textPreco.toString().toDouble()
        compra = Compra(quantidade, descricao, preco)
        compra.salva(autenticacao.currentUser?.email)
    }

    private fun validarCampos(textDescricao: Editable?, textQuantidade: Editable?, textPreco: Editable?): Boolean {
        if (!textDescricao.isNullOrEmpty()) {
            if (!textQuantidade.isNullOrEmpty()) {
                if (!textPreco.isNullOrEmpty()) {
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
}
