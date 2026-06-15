package de.valentinho13.catchlingo.feature.discover

import de.valentinho13.catchlingo.data.DiscoveredWord

internal data class VocabularyMatch(
    val id: String,
    val word: String,
    val source: String,
    val category: String,
    val minConfidence: Float = DefaultMinConfidence,
)

internal fun mapLabelToVocabulary(label: String): VocabularyMatch? {
    val normalized = label.lowercase()
    return Vocabulary.entries.firstOrNull { entry ->
        entry.matches(normalized)
    }?.toMatch()
}

internal fun VocabularyMatch.toDiscoveredWord(nowMillis: Long): DiscoveredWord = DiscoveredWord(
    id = id,
    word = word,
    source = source,
    category = category,
    discoveredAtMillis = nowMillis,
)

private enum class Vocabulary(
    val id: String,
    val word: String,
    val source: String,
    val category: String,
    val labels: Set<String>,
    val minConfidence: Float = DefaultMinConfidence,
) {
    // Essen & Trinken
    Cup(
        id = "cangkir",
        word = "cangkir",
        source = "cup",
        category = "Essen & Trinken",
        labels = setOf("cup", "mug", "coffee cup", "coffee mug", "drinkware"),
    ),
    Coffee(
        id = "kopi",
        word = "kopi",
        source = "coffee",
        category = "Essen & Trinken",
        labels = setOf("coffee", "espresso", "cappuccino", "latte"),
    ),
    Glass(
        id = "gelas",
        word = "gelas",
        source = "glass",
        category = "Essen & Trinken",
        labels = setOf("glass", "drinking glass", "tumbler"),
    ),
    Bottle(
        id = "botol",
        word = "botol",
        source = "bottle",
        category = "Essen & Trinken",
        labels = setOf("bottle", "water bottle"),
    ),
    Plate(
        id = "piring",
        word = "piring",
        source = "plate",
        category = "Essen & Trinken",
        labels = setOf("plate", "dish"),
    ),
    Bowl(
        id = "mangkuk",
        word = "mangkuk",
        source = "bowl",
        category = "Essen & Trinken",
        labels = setOf("bowl"),
    ),
    Spoon(
        id = "sendok",
        word = "sendok",
        source = "spoon",
        category = "Essen & Trinken",
        labels = setOf("spoon"),
    ),
    Fork(
        id = "garpu",
        word = "garpu",
        source = "fork",
        category = "Essen & Trinken",
        labels = setOf("fork"),
    ),
    Knife(
        id = "pisau",
        word = "pisau",
        source = "knife",
        category = "Essen & Trinken",
        labels = setOf("knife", "kitchen knife"),
    ),
    Apple(
        id = "apel",
        word = "apel",
        source = "apple",
        category = "Essen & Trinken",
        labels = setOf("apple"),
    ),
    Banana(
        id = "pisang",
        word = "pisang",
        source = "banana",
        category = "Essen & Trinken",
        labels = setOf("banana"),
    ),
    Bread(
        id = "roti",
        word = "roti",
        source = "bread",
        category = "Essen & Trinken",
        labels = setOf("bread", "toast"),
    ),
    Water(
        id = "air",
        word = "air",
        source = "water",
        category = "Essen & Trinken",
        labels = setOf("water", "drinking water"),
    ),

    // Zuhause
    Chair(
        id = "kursi",
        word = "kursi",
        source = "chair",
        category = "Zuhause",
        labels = setOf("chair"),
        minConfidence = 0.74f,
    ),
    Table(
        id = "meja",
        word = "meja",
        source = "table",
        category = "Zuhause",
        labels = setOf("table", "desk"),
    ),
    Sofa(
        id = "sofa",
        word = "sofa",
        source = "sofa",
        category = "Zuhause",
        labels = setOf("sofa", "couch"),
    ),
    Bed(
        id = "tempat_tidur",
        word = "tempat tidur",
        source = "bed",
        category = "Zuhause",
        labels = setOf("bed"),
    ),
    Laptop(
        id = "laptop",
        word = "laptop",
        source = "laptop",
        category = "Zuhause",
        labels = setOf("laptop", "notebook computer"),
    ),
    Notebook(
        id = "buku_catatan",
        word = "buku catatan",
        source = "notebook",
        category = "Zuhause",
        labels = setOf("notebook", "journal"),
    ),
    Book(
        id = "buku",
        word = "buku",
        source = "book",
        category = "Zuhause",
        labels = setOf("book"),
    ),
    Pen(
        id = "pena",
        word = "pena",
        source = "pen",
        category = "Zuhause",
        labels = setOf("pen", "ballpoint pen"),
    ),
    Pencil(
        id = "pensil",
        word = "pensil",
        source = "pencil",
        category = "Zuhause",
        labels = setOf("pencil"),
    ),
    Keyboard(
        id = "keyboard",
        word = "keyboard",
        source = "keyboard",
        category = "Zuhause",
        labels = setOf("keyboard", "computer keyboard"),
    ),
    Mouse(
        id = "mouse",
        word = "mouse",
        source = "computer mouse",
        category = "Zuhause",
        labels = setOf("computer mouse"),
    ),
    Computer(
        id = "komputer",
        word = "komputer",
        source = "computer",
        category = "Zuhause",
        labels = setOf("computer", "desktop computer", "personal computer"),
    ),
    Television(
        id = "televisi",
        word = "televisi",
        source = "television",
        category = "Zuhause",
        labels = setOf("television", "tv"),
    ),
    Lamp(
        id = "lampu",
        word = "lampu",
        source = "lamp",
        category = "Zuhause",
        labels = setOf("lamp", "lampshade", "light fixture"),
    ),
    Clock(
        id = "jam",
        word = "jam",
        source = "clock",
        category = "Zuhause",
        labels = setOf("clock"),
    ),
    Door(
        id = "pintu",
        word = "pintu",
        source = "door",
        category = "Zuhause",
        labels = setOf("door"),
    ),
    Window(
        id = "jendela",
        word = "jendela",
        source = "window",
        category = "Zuhause",
        labels = setOf("window"),
    ),

    // Unterwegs
    Bicycle(
        id = "sepeda",
        word = "sepeda",
        source = "bicycle",
        category = "Unterwegs",
        labels = setOf("bicycle", "bike", "cycling"),
    ),
    Car(
        id = "mobil",
        word = "mobil",
        source = "car",
        category = "Unterwegs",
        labels = setOf("car", "automobile"),
    ),
    Bus(
        id = "bus",
        word = "bus",
        source = "bus",
        category = "Unterwegs",
        labels = setOf("bus"),
    ),
    Train(
        id = "kereta",
        word = "kereta",
        source = "train",
        category = "Unterwegs",
        labels = setOf("train"),
    ),
    Motorcycle(
        id = "motor",
        word = "motor",
        source = "motorcycle",
        category = "Unterwegs",
        labels = setOf("motorcycle", "motorbike", "scooter"),
    ),
    Phone(
        id = "ponsel",
        word = "ponsel",
        source = "phone",
        category = "Unterwegs",
        labels = setOf("phone", "mobile phone", "cellphone", "cell phone", "smartphone"),
        minConfidence = 0.78f,
    ),
    Bag(
        id = "tas",
        word = "tas",
        source = "bag",
        category = "Unterwegs",
        labels = setOf("bag", "handbag", "backpack"),
    ),
    Suitcase(
        id = "koper",
        word = "koper",
        source = "suitcase",
        category = "Unterwegs",
        labels = setOf("suitcase", "luggage"),
    ),
    Umbrella(
        id = "payung",
        word = "payung",
        source = "umbrella",
        category = "Unterwegs",
        labels = setOf("umbrella"),
    ),
    Shoe(
        id = "sepatu",
        word = "sepatu",
        source = "shoe",
        category = "Unterwegs",
        labels = setOf("shoe", "sneaker", "footwear"),
    ),
    TrafficLight(
        id = "lampu_lalu_lintas",
        word = "lampu lalu lintas",
        source = "traffic light",
        category = "Unterwegs",
        labels = setOf("traffic light", "traffic signal"),
    ),
    Road(
        id = "jalan",
        word = "jalan",
        source = "road",
        category = "Unterwegs",
        labels = setOf("road", "street"),
    ),
    Bridge(
        id = "jembatan",
        word = "jembatan",
        source = "bridge",
        category = "Unterwegs",
        labels = setOf("bridge"),
    ),

    // Natur
    Plant(
        id = "tanaman",
        word = "tanaman",
        source = "plant",
        category = "Natur",
        labels = setOf("plant", "houseplant", "potted plant"),
    ),
    Flower(
        id = "bunga",
        word = "bunga",
        source = "flower",
        category = "Natur",
        labels = setOf("flower", "blossom"),
    ),
    Tree(
        id = "pohon",
        word = "pohon",
        source = "tree",
        category = "Natur",
        labels = setOf("tree"),
    ),
    Leaf(
        id = "daun",
        word = "daun",
        source = "leaf",
        category = "Natur",
        labels = setOf("leaf", "leaves"),
    ),
    Grass(
        id = "rumput",
        word = "rumput",
        source = "grass",
        category = "Natur",
        labels = setOf("grass", "lawn"),
    ),
    Sky(
        id = "langit",
        word = "langit",
        source = "sky",
        category = "Natur",
        labels = setOf("sky"),
    ),
    Cloud(
        id = "awan",
        word = "awan",
        source = "cloud",
        category = "Natur",
        labels = setOf("cloud"),
    ),
    Sun(
        id = "matahari",
        word = "matahari",
        source = "sun",
        category = "Natur",
        labels = setOf("sun"),
    ),
    Dog(
        id = "anjing",
        word = "anjing",
        source = "dog",
        category = "Natur",
        labels = setOf("dog", "puppy"),
        minConfidence = 0.86f,
    ),
    Cat(
        id = "kucing",
        word = "kucing",
        source = "cat",
        category = "Natur",
        labels = setOf("cat", "kitten"),
    ),
    Bird(
        id = "burung",
        word = "burung",
        source = "bird",
        category = "Natur",
        labels = setOf("bird"),
    );

    fun toMatch(): VocabularyMatch = VocabularyMatch(
        id = id,
        word = word,
        source = source,
        category = category,
        minConfidence = minConfidence,
    )

    fun matches(normalizedLabel: String): Boolean =
        if (id in ExactLabelMatchIds) {
            normalizedLabel in labels
        } else {
            labels.any { normalizedLabel.contains(it) }
        }
}

private const val DefaultMinConfidence = 0.62f
private val ExactLabelMatchIds = setOf("anjing", "kursi", "ponsel")
