package com.marcos.postresapp.data.remote.utils

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.ByteString
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * RequestBody que puede ser leído múltiples veces.
 * Útil para peticiones que pueden ser reintentadas por el Authenticator.
 */
class ReusableRequestBody private constructor(
    private val mediaType: MediaType?,
    private val content: ByteArray
) : RequestBody() {

    override fun contentType(): MediaType? = mediaType

    override fun contentLength(): Long = content.size.toLong()

    override fun writeTo(sink: BufferedSink) {
        sink.write(content)
    }

    companion object {
        fun create(mediaType: MediaType?, inputStream: InputStream): ReusableRequestBody {
            val content = inputStream.use { it.readBytes() }
            return ReusableRequestBody(mediaType, content)
        }

        fun create(mediaType: MediaType?, byteArray: ByteArray): ReusableRequestBody {
            return ReusableRequestBody(mediaType, byteArray)
        }

        fun create(mediaType: MediaType?, string: String): ReusableRequestBody {
            return ReusableRequestBody(mediaType, string.toByteArray())
        }
    }
}