package vo

/**
 *
 * @author linxixin@cvte.com
 */
data class BaseException(internal val msg: String) : RuntimeException(msg)

data class MethodVo(
        var packageName: String?=null,
        var className: String?=null,
        var methodName:String?=null,
        val paramNames: List<String>?= mutableListOf()
)
