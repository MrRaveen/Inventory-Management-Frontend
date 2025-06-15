package Model
import com.google.gson.annotations.SerializedName
//class Folders {
//    var name = ""
//    var description = ""
//    var folderID = 0
//    var userID = 0
//    var userAccounts = null
//}
data class Folders(
    @SerializedName("folderID")
    val folderID: Int = 0,

    @SerializedName("folderName")
    val name: String = "",

    @SerializedName("descriptionFolder")
    val description: String = "",

    @SerializedName("userID")
    val userID: Int = 0,

    @SerializedName("userAccounts")
    val userAccounts: Any? = null
)