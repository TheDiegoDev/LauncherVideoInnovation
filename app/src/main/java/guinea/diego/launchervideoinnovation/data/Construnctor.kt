
import guinea.diego.launchervideoinnovation.data.ValuesTVRepository

object Single{
    val valuesRepository: ValuesTVRepository = ValuesTVRepository()
    fun tvRepository(): ValuesTVRepository {return valuesRepository}
}
