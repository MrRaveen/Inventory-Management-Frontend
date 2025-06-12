package controller

class logInEndpoints {
    private val _logInEndpoint : String = "https://192.168.159.42:7113/logIn"
    private val _createAccEndpoint : String = "https://192.168.159.42:7113/createAccount"
    var logInEndpoint : String
            get() = _logInEndpoint
            set(value){
                logInEndpoint = _logInEndpoint
            }
    var createAccEndpoint : String
        get() = _createAccEndpoint
        set(value){
            createAccEndpoint = _createAccEndpoint
        }
}