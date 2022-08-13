package cn.lqlklu.tools.fileshare.util

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Throws(IOException::class)
private fun InputStream.copyTo(out: OutputStream) {
    val b = ByteArray(8192)
    var r: Int
    while (this.read(b).also { r = it } != -1) {
        out.write(b, 0, r)
    }
}
