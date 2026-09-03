package com.fire.mangareader.data.network.interceptor

import com.fire.mangareader.domain.model.parser.IpMapping
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor that bypasses DNS by routing directly to a resolved IP address,
 * while preserving the original Host header to bypass Cloudflare/SNI blocks.
 */
class DirectIpInterceptor(private var ipMapping: IpMapping? = null) : Interceptor {
    
    fun updateMapping(mapping: IpMapping?) {
        this.ipMapping = mapping
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        ipMapping?.let { mapping ->
            val originalHost = request.url.host
            val overrideIp = mapping.overrides[originalHost] ?: mapping.defaultIp
            
            if (overrideIp.isNotEmpty()) {
                val newUrl = request.url.newBuilder().host(overrideIp).build()
                request = request.newBuilder()
                    .url(newUrl)
                    .header("Host", originalHost) // Preserve Host for the target server
                    .build()
            }
        }
        
        return chain.proceed(request)
    }
}
