package ie.koala.topics.feature.user

class User {
    var ticker: String? = null
    var price: Float = 0.toFloat()

    override fun toString(): String {
        return "{User ticker=$ticker price=$price}"
    }
}