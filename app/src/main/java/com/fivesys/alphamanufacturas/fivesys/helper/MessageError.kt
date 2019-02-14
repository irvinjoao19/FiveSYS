package com.fivesys.alphamanufacturas.fivesys.helper

open class MessageError {

    var Message: String? = null
    var ExceptionMessage: String? = null
    var ExceptionType: String? = null
    var StackTrace: String? = null
    var Error: String? = null

    constructor()

    constructor(Message: String?, ExceptionMessage: String?, ExceptionType: String?, StackTrace: String?, Error: String?) {
        this.Message = Message
        this.ExceptionMessage = ExceptionMessage
        this.ExceptionType = ExceptionType
        this.StackTrace = StackTrace
        this.Error = Error
    }
}