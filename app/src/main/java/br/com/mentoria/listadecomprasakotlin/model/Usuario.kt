package br.com.mentoria.listadecomprasakotlin.model

import com.google.firebase.database.FirebaseDatabase

class Usuario {

    var id = ""
    var nome = ""
    var email = ""

    fun salva() {
        var database = FirebaseDatabase.getInstance().reference
        database.child("usuarios").child(id).setValue(this)
    }

}