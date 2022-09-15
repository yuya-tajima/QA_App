package jp.techacademy.yuya.tajima.qa_app

import java.io.Serializable

class Favorite (val title: String, val name: String, val bytes: ByteArray) : Serializable {
    val imageBytes: ByteArray = bytes.clone()
}
