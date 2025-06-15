package controller

class logInEndpoints {
    private val _logInEndpoint: String = "https://192.168.159.42:7113/logIn"
    private val _createAccEndpoint: String = "https://192.168.159.42:7113/createAccount"
    private val _getFolders: String = "https://192.168.159.42:7113/getFoldersByUser/"

    // Use immutable vals instead of vars with unnecessary setters
    val logInEndpoint: String get() = _logInEndpoint
    val createAccEndpoint: String get() = _createAccEndpoint
    val getFoldersEndpoint: String get() = _getFolders  // CORRECTED
}