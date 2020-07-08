package ksci

enum class DataType {
    INT, FLOAT, DOUBLE, STRING;

    fun fromString(s: String): Any = when (this) {
        INT -> s.toInt()
        FLOAT -> s.toFloat()
        DOUBLE -> s.toDouble()
        STRING -> s
    }

    fun accept(v: Any?) = when (this) {
        INT -> v is Int?
        FLOAT -> v is Float?
        DOUBLE -> v is Double?
        STRING -> v is String?
    }

    companion object {
        inline fun <reified T> valueOf() = when (T::class.simpleName) {
            "Int" -> INT
            "Float" -> FLOAT
            "Double" -> DOUBLE
            "String" -> STRING
            else -> throw IllegalArgumentException("Unknown type: ${T::class}")
        }
    }
}



