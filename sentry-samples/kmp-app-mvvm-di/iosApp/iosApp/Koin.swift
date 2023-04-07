import Foundation
import shared

func startKoin() {
    let koinApplication = KoinIOSKt.doInitKoinIos()
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin?
var koin: Koin_coreKoin {
    return _koin!
}