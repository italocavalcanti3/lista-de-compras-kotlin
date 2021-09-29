package br.com.mentoria.listadecomprasakotlin.helper

import android.util.Base64

class Base64Custom {

    companion object {

        fun codificar(texto: String): String {
            return Base64.encodeToString(texto.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                .replace("(\\n|\\r)", "")
        }

        fun decodificar(texto: String): String {
            return String(Base64.decode(texto, Base64.NO_WRAP))
        }

    }

}