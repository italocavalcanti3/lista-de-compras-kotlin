package br.com.mentoria.listadecomprasakotlin.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import br.com.mentoria.listadecomprasakotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide

class MainActivity : IntroActivity() {

    private lateinit var autenticacao: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        autenticacao = Firebase.auth

        isButtonBackVisible = false
        isButtonNextVisible = false
        isFullscreen = true

        addSlide(
            FragmentSlide.Builder()
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .fragment(R.layout.slide_1_fragment)
                .canGoBackward(false)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .fragment(R.layout.slide_2_fragment)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .fragment(R.layout.slide_3_fragment)
                .build()
        )
        addSlide(
            FragmentSlide.Builder()
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .fragment(R.layout.slide_4_fragment)
                .canGoForward(false)
                .build()
        )
    }

    override fun onStart() {
        super.onStart()
        verificaUsuarioLogado()
    }

    private fun verificaUsuarioLogado() {
        val currentUser = autenticacao.currentUser
        if (currentUser != null) {
            iniciarActivityPrincipal()
        }
    }


    private fun iniciarActivityPrincipal() {
        val intent = Intent(this, ListaComprasActivity::class.java)
        startActivity(intent)
    }

    fun abrirTelaCadastro(view: View) {
        val intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
    }

    fun jaTenhoConta(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}