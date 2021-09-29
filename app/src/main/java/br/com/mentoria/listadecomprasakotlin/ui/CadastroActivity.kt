package br.com.mentoria.listadecomprasakotlin.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.mentoria.listadecomprasakotlin.databinding.ActivityCadastroBinding
import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import br.com.mentoria.listadecomprasakotlin.model.Usuario
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CadastroActivity : AppCompatActivity() {

    private var autenticacao = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Cadastro"

        val email = binding.inputCadastroEmail.text
        val nome = binding.inputCadastroNome.text
        val senha = binding.inputCadastroSenha.text
        val botao = binding.buttonCadastrar

        configuraBotao(botao, nome, email, senha)

    }

    private fun configuraBotao(
        botao: Button,
        nome: Editable,
        email: Editable,
        senha: Editable
    ) {
        botao.setOnClickListener {
            val validaCampos = validarCampos(nome, email, senha)
            if (validaCampos) {
                cadastraUsuarioFirebase(nome, email, senha)
                abrirTelaPrincipal()
            }
        }
    }

    private fun abrirTelaPrincipal() {
        val intent = Intent(this, ListaComprasActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun cadastraUsuarioFirebase(nome: Editable, email: Editable, senha: Editable) {
        autenticacao.createUserWithEmailAndPassword(email.toString(), senha.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val usuario = Usuario()
                    usuario.email = email.toString()
                    usuario.nome = nome.toString()
                    usuario.id = Base64Custom.codificar(email.toString())
                    usuario.salva()
                } else {
                    val erro: String
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        erro = "Digite uma senha mais forte";
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        erro = "E-mail inválido";
                    } catch (e: FirebaseAuthUserCollisionException) {
                        erro = "Conta já cadastrada";
                    } catch (e: Exception) {
                        erro = "Erro ao cadastrar usuário";
                        e.printStackTrace();
                    }
                    Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun validarCampos(nome: Editable, email: Editable, senha: Editable): Boolean {
        if (nome.isNotEmpty()) {
            if (email.isNotEmpty()) {
                if (senha.isNotEmpty()) {
                    return true
                } else {
                    Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha o e-mail", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Preencha o nome", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    fun abrirTelaLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}