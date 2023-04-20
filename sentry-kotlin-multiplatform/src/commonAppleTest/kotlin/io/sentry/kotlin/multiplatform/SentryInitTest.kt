package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSURLAuthenticationChallenge
import platform.Foundation.NSURLAuthenticationMethodServerTrust
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionAuthChallengeDisposition
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionDelegateProtocol
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.serverTrust
import platform.Security.SecCertificateCopyData
import platform.Security.SecTrustEvaluate
import platform.Security.SecTrustGetCertificateAtIndex
import platform.Security.SecTrustResultType
import platform.Security.SecTrustResultTypeVar
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertTrue

class NSURLSessionDelegate : NSObject(), NSURLSessionDelegateProtocol {

    override fun URLSession(
        session: NSURLSession,
        didReceiveChallenge: NSURLAuthenticationChallenge,
        completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential?) -> Unit
    ) {
        val serverTrust = didReceiveChallenge.protectionSpace.serverTrust
        var result: SecTrustResultType = 0u

        memScoped {
            val nativeResult = alloc<SecTrustResultTypeVar>()
            nativeResult.value = result
            SecTrustEvaluate(serverTrust!!, nativeResult.ptr)
        }

        val serverCertificate = SecTrustGetCertificateAtIndex(serverTrust, 0)
        val serverCertificateData = SecCertificateCopyData(serverCertificate)
        val data = CFDataGetBytePtr(serverCertificateData)
        val size = CFDataGetLength(serverCertificateData)

        val cert1 = NSData.dataWithBytes(data, size.convert())
        val pathToCert = "/Users/giancarlobuenaflor/Desktop/ingest.sentry.io.cer"

        val localCertificate: NSData = NSData.dataWithContentsOfFile(pathToCert!!)!!

        println(localCertificate)
        println(cert1)

        completionHandler(NSURLSessionAuthChallengeUseCredential, NSURLCredential.create(didReceiveChallenge.protectionSpace.serverTrust))
    }
}

class NSURLSessionDelegate2 : NSObject(), NSURLSessionDelegateProtocol {
    override fun URLSession(
        session: NSURLSession,
        didReceiveChallenge: NSURLAuthenticationChallenge,
        completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential?) -> Unit
    ) {
        if (didReceiveChallenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust) {
            val serverTrust = didReceiveChallenge.protectionSpace.serverTrust
            completionHandler(NSURLSessionAuthChallengeUseCredential, NSURLCredential.create(serverTrust))
        } else {
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
        }
    }
}

class SentryInitTest {

    private suspend fun captureMessageAndWait(): Boolean {
        return suspendCoroutine { continuation ->
            Sentry.captureMessage("hello")
            GlobalScope.launch {
                delay(10000)
                continuation.resume(true)
            }
        }
    }

    @Test
    fun `SentryInit Test`() = runBlocking {
        SentrySDK.start {
            it?.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
            it?.urlSessionDelegate = NSURLSessionDelegate()
            it?.attachStacktrace = true
            it?.debug = true
        }

        val delegate = NSURLSessionDelegate2()
        val session = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration,
            delegate = delegate,
            delegateQueue = null
        )

        val url = NSURL(string = "https://httpbin.org/status/404")
        val request = NSURLRequest(uRL = url)

        session.dataTaskWithRequest(request) { data, response, error ->
            if (error != null) {
                // handle error
                println("error: $error")
            } else {
                // handle successful response
                println("response: $response")
            }
        }.resume()

        println("bundles: ${NSBundle.mainBundle.infoDictionary?.get("CFBundleIdentifier")}")

        val captureComplete = captureMessageAndWait()

        assertTrue(captureComplete)
    }
}
