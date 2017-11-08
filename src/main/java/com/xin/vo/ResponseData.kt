package vo

/**
 *
 * @author linxixin@cvte.com
 */
data class ResponseData(var dbDetailInfoVo: DbDetailInfoVo? = null
                        , var sqls: List<String>? = mutableListOf())


data class DbDetailInfoVo(var url: String? = null
                          , var host: String? = null
                          , var port: Int? = null
                          , var username: String? = null
                          , var password: String? = null
                          , var dbName: String? = null)