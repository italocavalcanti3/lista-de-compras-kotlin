package br.com.mentoria.listadecomprasakotlin.model

import android.annotation.SuppressLint
import android.util.Log
import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import com.google.firebase.database.*
import java.io.Serializable

class Compra : Serializable {

    var idCompra: String = ""
    var quantidade: Int = 0
    var descricao: String = ""
    var preco: Double = 0.00
    var total: Double = 0.00

//    companion object {
//        @Exclude @get:Exclude @set:Exclude
//        var idGeral: Int = 1
//    }

    constructor()

    constructor(id: String, quantidade: Int, descricao: String, preco: Double) {
        this.quantidade = quantidade
        this.descricao = descricao
        this.preco = preco
        this.total = quantidade * preco
        this.idCompra = id
    }

    fun salva(email: String?) {
        val idUsuario = Base64Custom.codificar(email.toString())
        val database = FirebaseDatabase.getInstance().reference
        database.child("lista").child(idUsuario).child(this.idCompra.toString()).setValue(this)
    }

}