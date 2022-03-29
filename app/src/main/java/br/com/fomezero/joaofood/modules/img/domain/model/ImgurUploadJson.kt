package br.com.fomezero.joaofood.modules.img.domain.model

import com.google.gson.annotations.SerializedName

data class ImgurUploadJson(
    @SerializedName("data") val data: Data?,
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val success: Boolean?
) {
    fun getImageLink(): String? {
        return data?.link
    }
}