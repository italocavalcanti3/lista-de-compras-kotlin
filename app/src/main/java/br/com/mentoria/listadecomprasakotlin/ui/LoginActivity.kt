package br.com.mentoria.listadecomprasakotlin.ui
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import br.com.mentoria.listadecomprasakotlin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private var autenticacao = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Login"

        val email = binding.inputLoginEmail.text
        val senha = binding.inputLoginSenha.text
        val botao = binding.buttonEntrar

        configuraBotao(botao, email, senha)

    }

    private fun configuraBotao(
        botao: Button,
        email: Editable,
        senha: Editable
    ) {
        botao.setOnClickListener {
            val validaCampos = validarCampos(email, senha)
            if (validaCampos) {
                logaUsuarioFirebase(email, senha)
            }
        }
    }

    private fun abrirTelaPrincipal() {
        var intent = Intent(this, ListaComprasActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun logaUsuarioFirebase(email: Editable, senha: Editable) {
        autenticacao = FirebaseAuth.getInstance()
        autenticacao.signInWithEmailAndPassword(email.toString(), senha.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                abrirTelaPrincipal()
            } else {
                var erro: String
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    erro = "E-mail ou senha inválidos"
                } catch (e: FirebaseAuthInvalidUserException) {
                    erro = "E-mail ou senha inválidos"
                } catch (e: Exception) {
                    erro = "Erro ao logar"
                    e.printStackTrace()
                }
                Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun validarCampos(email: Editable, senha: Editable): Boolean {
        if (email.isNotEmpty()) {
            if (senha.isNotEmpty()) {
                return true
            } else {
                Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Preencha a e-mail", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    fun abrirTelaCadastro(view: View) {
        var intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
        finish()
    }

}