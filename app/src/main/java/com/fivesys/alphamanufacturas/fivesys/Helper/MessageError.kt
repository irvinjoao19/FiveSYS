package com.calida.dsige.lectura.Helper

open class MessageError {

    var Message: String? = null
    var ExceptionMessage: String? = null
    var ExceptionType: String? = null
    var StackTrace: String? = null

    constructor()

    constructor(Message: String?, ExceptionMessage: String?, ExceptionType: String?, StackTrace: String?) {
        this.Message = Message
        this.ExceptionMessage = ExceptionMessage
        this.ExceptionType = ExceptionType
        this.StackTrace = StackTrace
    }


}