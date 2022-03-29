package br.com.fomezero.joaofood.model

data class MerchantData(
    val name: String,
    val phoneNumber: String,
    val email:String? = null,  // TODO: Change to lat long
    val location: String? = null,
    val imageUrl: String?=null,
)
