package com.visura.domain.vo.imovel

    data class Imovel(
        val id: Int,
        val tipoImovel: String,
        val criadoEm: String,
        val cep: String,
        val rua: String,
        val numero: Int,
        val bairro: String,
        val cidade: String,
        val estado: String,
        val complemento: String? = null
    ){

        val cepLimpo: String
            get() = this.cep.replace(Regex("[^0-9]"), "")
        val cepFormatado: String
            get(){
                val cepLimpo = this.cepLimpo
                return if (cepLimpo.length == 8){
                    "${cepLimpo.substring(0, 5)}-${cepLimpo.substring(5)}"
                } else {
                    cepLimpo
                }
            }
    }