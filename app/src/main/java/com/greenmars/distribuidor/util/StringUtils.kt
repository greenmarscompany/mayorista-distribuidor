package com.greenmars.distribuidor.util

class StringUtils {
    companion object {
        fun truncarTexto(texto: String, maxLength: Int): String {
            return if (texto.length > maxLength) {
                texto.substring(0, maxLength) + "..."
            } else {
                texto
            }
        }
    }

}