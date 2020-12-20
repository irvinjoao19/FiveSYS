package com.fivesys.alphamanufacturas.fivesys.helper

open class MessageError {

    var Message: String = ""
    var ExceptionMessage: String= ""
    var ExceptionType: String= ""
    var StackTrace: String= ""
    var Error: String= ""

    constructor()

    constructor(Message: String, ExceptionMessage: String, ExceptionType: String, StackTrace: String, Error: String) {
        this.Message = Message
        this.ExceptionMessage = ExceptionMessage
        this.ExceptionType = ExceptionType
        this.StackTrace = StackTrace
        this.Error = Error
    }
}