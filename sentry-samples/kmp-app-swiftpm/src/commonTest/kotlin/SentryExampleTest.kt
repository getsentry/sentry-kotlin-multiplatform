import kotlin.test.Test

class SentryExampleTest {
    @Test
    fun captureWithoutInitializationIsSafe() {
        captureExample()
    }
}
