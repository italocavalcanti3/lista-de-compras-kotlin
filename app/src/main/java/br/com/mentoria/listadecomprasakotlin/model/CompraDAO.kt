package br.com.mentoria.listadecomprasakotlin.model

import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CompraDAO {

    val autenticacao = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var lista: ArrayList<Compra> = arrayListOf()


}