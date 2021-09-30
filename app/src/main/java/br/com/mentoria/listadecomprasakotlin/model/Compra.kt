package br.com.mentoria.listadecomprasakotlin.model

import br.com.mentoria.listadecomprasakotlin.helper.Base64Custom
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

class Compra: Serializable {

    var idCompra: Int = 0
    var quantidade: Int = 0
    var descricao: String = ""
    var preco: Double = 0.00
    var total: Double = 0.00

    companion object {
        @get:Exclude
        var valorTotalLista: Double = 0.00
        @get:Exclude
        var idGeral: Int = 1
    }
    fun ajustaValorTotalLista(valor: Double) {
        valorTotalLista = valor
    }

    fun getValorTotalLista(): Double {
        return valorTotalLista
    }
    fun ajustaIdGeral(id: Int) {
        idGeral = 0
    }

    constructor()

    constructor(quantidade: Int, descricao: String, preco: Double) {
        this.quantidade = quantidade
        this.descricao = descricao
        this.preco = preco
        this.total = quantidade * preco
        valorTotalLista += total
        this.idCompra = idGeral
        idGeral++
    }

    fun salva(email: String?) {
        val idUsuario = Base64Custom.codificar(email.toString())
        val database = FirebaseDatabase.getInstance().reference
        database.child("lista").child(idUsuario).child(this.idCompra.toString()).setValue(this)
        database.child("lista").child(idUsuario).child("saldo").setValue(valorTotalLista)
//        database.child("saldo").child(idUsuario).setValue(valorTotalLista)
    }

}