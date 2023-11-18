package sample.kmp.app

import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithRequest

@Suppress("Unused") // Called from Swift
fun captureHttpClientError() {
  val url = NSURL(string = "https://httpbin.org/status/404")
  val request = NSURLRequest(uRL = url)
  NSURLSession.sharedSession
      .dataTaskWithRequest(request) { data, response, error ->
        if (error != null) {
          // handle error
          println("error: $error")
        } else {
          // handle successful response
          println("response: $response")
        }
      }
      .resume()
}
