package controller

class logInEndpoints {
    private val _logInEndpoint : String = "https://localhost:7113/logIn"
        var logInEndpoint : String
            get() = _logInEndpoint
            set(value){
                logInEndpoint = _logInEndpoint
            }
}