package br.com.fomezero.joaofood.model

data class OngData(
    val name: String,
    val phoneNumber: String,
    val imageUrl: String? = null,
    val description: String? = null,

    // TODO: Lat log
    val siteUrl: String? = null,
    val isApproved: Boolean? = false,
)