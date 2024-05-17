package sample.kmp.integrationtest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform