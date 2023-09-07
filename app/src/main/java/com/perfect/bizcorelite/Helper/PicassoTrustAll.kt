package com.perfect.bizcorelite.Helper

import android.content.Context
import android.util.Log
import com.perfect.bizcorelite.Api.ApiService
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

class PicassoTrustAll private constructor(context: Context) {
    @Throws(
            CertificateException::class,
            KeyStoreException::class,
            IOException::class,
            NoSuchAlgorithmException::class,
            KeyManagementException::class
    )
    private fun getSSLSocketFactory(context: Context): SSLSocketFactory {
        val sslcertificate = ApiService.CERT_NAME

        val cf = CertificateFactory.getInstance("X.509")
//        val caInput = context!!.assets.open(Config.CERT_NAME)
        val caInput = context!!.assets.open(sslcertificate!!)
        val ca = cf.generateCertificate(caInput)
        caInput.close()
        val keyStore = KeyStore.getInstance("BKS")
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)
        val wrappedTrustManagers = getWrappedTrustManagers(tmf.trustManagers)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, wrappedTrustManagers, null)
        return sslContext.socketFactory
    }

    private fun getWrappedTrustManagers(trustManagers: Array<TrustManager>): Array<TrustManager> {
        val originalTrustManager = trustManagers[0] as X509TrustManager
        return arrayOf(
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return originalTrustManager.acceptedIssuers
                    }

                    override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
                        try {
                            if (certs != null && certs.size > 0) {
                                certs[0].checkValidity()
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType)
                            }
                        } catch (e: CertificateException) {
                            Log.w("checkClientTrusted", e.toString())
                        }
                    }

                    override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
                        try {
                            if (certs != null && certs.size > 0) {
                                certs[0].checkValidity()
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType)
                            }
                        } catch (e: CertificateException) {
                            Log.w("checkServerTrusted", e.toString())
                        }
                    }
                }
        )
    }

    companion object {
        private var mInstance: Picasso? = null
        fun getInstance(context: Context): Picasso? {
            if (mInstance == null) {
                PicassoTrustAll(context)
            }
            return mInstance
        }
    }

    init {
        val client = com.squareup.okhttp.OkHttpClient()
        client.hostnameVerifier = HostnameVerifier { s, sslSession -> true }
        try {
            client.sslSocketFactory = getSSLSocketFactory(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mInstance = Picasso.Builder(context)
                .downloader(OkHttpDownloader(client))
                .listener { picasso, uri, exception -> Log.e("PICASSO", exception.toString()) }
                .build()
    }
}