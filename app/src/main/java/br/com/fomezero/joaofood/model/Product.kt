package br.com.fomezero.joaofood.model

import java.util.*


data class Product(
    val name: String,
    val amount: String,
    val price: String,
    val imageUrl: String?,
    val merchantData: MerchantData,
    val postDate: String,
)
