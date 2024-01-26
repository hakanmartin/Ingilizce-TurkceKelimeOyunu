package com.example.kotlinproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    words: List<String>,
    optionsMap: Map<String, List<String>>,
    correctAnswers: Map<String, String>, // Doğru cevapları saklayan Map
    onAnswerSelected: (Boolean) -> Unit, // Doğru cevap kontrolünü döndüren callback
    onGameFinished: () -> Unit // Oyunun bitişini bildiren callback
) {
    var currentWordIndex by remember { mutableStateOf(0) }
    var currentWord by remember(currentWordIndex) { mutableStateOf(words[currentWordIndex]) }
    var currentOptions by remember(currentWord) {
        mutableStateOf(optionsMap[currentWord] ?: emptyList())
    }

    var correctAnswersCount by remember { mutableStateOf(0) } // Doğru cevap sayacı
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.blback1),
            contentDescription = "description",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentWord,
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            currentOptions.forEach { option ->
                Button(
                    onClick = {
                        val isCorrectAnswer = correctAnswers[currentWord] == option
                        if (isCorrectAnswer) {
                            correctAnswersCount++
                        }
                        onAnswerSelected(isCorrectAnswer)

                        if (currentWordIndex < words.size - 1) {
                            currentWordIndex++
                            currentWord = words[currentWordIndex]
                        } else {
                            currentWordIndex = 0
                            currentWord = words[currentWordIndex]
                            onGameFinished() // Oyun bittiğinde callback'i çağır
                        }
                        currentOptions = optionsMap[currentWord] ?: emptyList()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(8.dp)
                ) {
                    Text(text = option, style = TextStyle(fontSize = 18.sp))
                }
            }
            Text(
                text = "Doğru Cevaplar: $correctAnswersCount",
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ResultScreen(
    correctAnswersCount: Int,
    onPlayAgainClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.blback1),
            contentDescription = "description3",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tebrikler!",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Text(
                text = "Doğru Cevaplar: $correctAnswersCount",
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        onPlayAgainClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(text = "Tekrar")
                }
                Button(
                    onClick = {
                        onMenuClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(text = "Menü")
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    private var isGameStarted by mutableStateOf(false)
    private var correctAnswersCount by mutableStateOf(0)
    private var currentWordIndex by mutableStateOf(0)
    private var isGameOver by mutableStateOf(false)
    private lateinit var selectedLevelWords: List<String>
    private lateinit var selectedLevelOptionsMap: Map<String, List<String>>
    private lateinit var selectedLevelCorrectAnswers: Map<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (!isGameStarted) {
                        MainScreen(
                            onLevelSelected = { words, optionsMap, correctAnswers ->
                                isGameStarted = true
                                correctAnswersCount = 0
                                currentWordIndex = 0
                                isGameOver = false

                                selectedLevelWords = words
                                selectedLevelOptionsMap = optionsMap
                                selectedLevelCorrectAnswers = correctAnswers
                            }
                        )
                    } else {
                        if (!isGameOver) {
                            GameScreen(
                                words = selectedLevelWords,
                                optionsMap = selectedLevelOptionsMap,
                                correctAnswers = selectedLevelCorrectAnswers,
                                onAnswerSelected = { isCorrect ->
                                    if (isCorrect) {
                                        correctAnswersCount++
                                    }
                                },
                                onGameFinished = {
                                    isGameOver = true
                                }
                            )
                        } else {
                            ResultScreen(
                                correctAnswersCount = correctAnswersCount,
                                onPlayAgainClick = {
                                    isGameOver = false
                                    correctAnswersCount = 0
                                },
                                onMenuClick = {
                                    isGameStarted = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onLevelSelected: (List<String>, Map<String, List<String>>, Map<String, String>) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.blback1),
            contentDescription = "description2",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kelime Oyunu",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .padding(bottom = 304.dp, top = 16.dp)
            )
            Text(
                text = "Başlamak için bir seviye seçin",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .padding(bottom = 16.dp, top = 4.dp)
            )

            Button(
                onClick = {
                    // A1 seviye için gerekli bilgileri gönder
                    onLevelSelected(
                        listOf(
                            "black", "car", "apple", "house", "woman", "cat", "who", "water", "bus",
                            "tree", "and", "dog", "but", "bread", "chair", "they", "yellow",
                            "this", "we", "name", "you", "now"
                        ), // A1 seviye kelimeleri
                        mapOf(
                            "black" to listOf("beyaz", "siyah", "yeşil", "mavi"),
                            "car" to listOf("araba", "bisiklet", "uçak", "gemi"),
                            "apple" to listOf("kiraz", "elma", "muz", "çilek"),
                            "house" to listOf("depo", "fare", "garaj", "ev"),
                            "woman" to listOf("yetişkin", "çocuk", "kadın", "erkek"),
                            "cat" to listOf("köpek", "kedi", "şapka", "bebek"),
                            "who" to listOf("nasıl", "neden", "kim", "ne"),
                            "water" to listOf("elma", "ekmek", "süt", "su"),
                            "bus" to listOf("tekne", "otobus", "taksi", "uçak"),
                            "tree" to listOf("dağ", "yağmur", "ağaç", "deniz"),
                            "and" to listOf("ve", "ama", "ne", "nasıl"),
                            "dog" to listOf("kedi", "kuş", "köpek", "kalem"),
                            "but" to listOf("evet", "çok", "ama", "hayır"),
                            "bread" to listOf("ekmek", "elma", "su", "et"),
                            "chair" to listOf("masa", "sandalye", "dolap", "halı"),
                            "they" to listOf("biz", "ben", "siz", "onlar"),
                            "yellow" to listOf("sarı", "mor", "yeşil", "beyaz"),
                            "this" to listOf("bunlar", "bu", "onlar", "biz"),
                            "we" to listOf("onlar", "biz", "siz", "ben"),
                            "name" to listOf("gün", "şimdi", "isim", "bilet"),
                            "you" to listOf("ben", "onlar", "biz", "sen"),
                            "now" to listOf("gün", "şimdi", "isim", "bilet"),
                        ), // A1 seviye cevapları
                        mapOf(
                            "black" to "siyah",
                            "car" to "araba",
                            "apple" to "elma",
                            "house" to "ev",
                            "woman" to "kadın",
                            "cat" to "kedi",
                            "who" to "kim",
                            "water" to "su",
                            "bus" to "otobüs",
                            "tree" to "ağaç",
                            "and" to "ve",
                            "dog" to "köpek",
                            "but" to "ama",
                            "bread" to "ekmek",
                            "chair" to "sandalye",
                            "they" to "onlar",
                            "yellow" to "sarı",
                            "this" to "bu",
                            "we" to "biz",
                            "name" to "isim",
                            "you" to "sen",
                            "now" to "şimdi"
                        ) // A1 seviye doğru cevapları
                    )
                },
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp)
                    .padding(8.dp)
            ) {
                Text(text = "A1", style = TextStyle(fontSize = 18.sp))
            }

            Button(
                onClick = {
                    // A2-B1 seviye için gerekli bilgileri gönder
                    onLevelSelected(
                        listOf(
                            "forever", "bold", "expensive", "kind", "passenger", "advanced", "colour", "computer",
                            "easy", "very", "feel", "garden", "bicycle", "family", "woman", "milk", "bird",
                            "phone", "man", "hat", "banana", "school"
                        ), // A2-B1 seviye kelimeleri
                        mapOf(
                            "forever" to listOf("gelişmiş", "bağış", "ebediyen", "gelecek"),
                            "bold" to listOf("cömert", "cesur", "bencil", "kibar"),
                            "expensive" to listOf("ağır", "ücret", "pahalı", "renkli"),
                            "kind" to listOf("cömert", "cesur", "bencil", "kibar"),
                            "passenger" to listOf("yolcu", "cömert", "insan", "kolay"),
                            "advanced" to listOf("gelecek", "bağış", "ebediyen", "gelişmiş"),
                            "colour" to listOf("renk", "ücret", "pahalı", "ağır"),
                            "computer" to listOf("buz dolabı", "bilgisayar", "telefon", "fırın"),
                            "easy" to listOf("saydam", "sade", "kolay", "silmek"),
                            "very" to listOf("cesur", "farklı", "çok", "çeşit"),
                            "feel" to listOf("silmek", "hissetmek", "aramak", "duymak"),
                            "garden" to listOf("bahçe", "orman", "göl", "deniz"),
                            "bicycle" to listOf("araba", "uçak", "bisiklet", "gemi"),
                            "family" to listOf("ağaç", "araba", "aile", "adam"),
                            "woman" to listOf("yetişkin", "çocuk", "kadın", "erkek"),
                            "milk" to listOf("su", "elma", "ekmek", "süt"),
                            "bird" to listOf("kedi", "kuş", "köpek", "kalem"),
                            "phone" to listOf("dolap", "masa", "telefon", "alet"),
                            "man" to listOf("kadın", "çocuk", "bebek", "adam"),
                            "hat" to listOf("ayakkabı", "pantolon", "şapka", "tişört"),
                            "banana" to listOf("elma", "muz", "çilek", "portakal"),
                            "school" to listOf("okul", "hastane", "ev", "hotel"),
                        ), // A2-B1 seviye cevapları
                        mapOf(
                            "forever" to "ebediyen",
                            "bold" to "cesur",
                            "expensive" to "pahalı",
                            "kind" to "kibar",
                            "passenger" to "yolcu",
                            "advanced" to "gelişmiş",
                            "colour" to "renk",
                            "computer" to "bilgisayar",
                            "easy" to "kolay",
                            "very" to "çok",
                            "feel" to "hissetmek",
                            "garden" to "bahçe",
                            "bicycle" to "bisiklet",
                            "family" to "aile",
                            "woman" to "kadın",
                            "milk" to "süt",
                            "bird" to "kuş",
                            "phone" to "telefon",
                            "man" to "adam",
                            "hat" to "şapka",
                            "banana" to "muz",
                            "school" to "okul"
                        ) // A2-B1 seviye doğru cevapları
                    )
                },
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp)
                    .padding(8.dp)
            ) {
                Text(text = "A2-B1", style = TextStyle(fontSize = 18.sp))
            }

            Button(
                onClick = {
                    // B2+ seviye için gerekli bilgileri gönder
                    onLevelSelected(
                        listOf(
                            "gambling", "frighten", "generous", "delicate", "fame", "perfect", "impatient",
                            "indicate", "manner", "precise", "proposal", "rapid", "remarkable", "seek",
                            "mere", "enthusiastic", "emphasize", "destruction", "awesome", "disaster",
                            "contribute", "recall"
                        ), // B2+ seviye kelimeleri
                        mapOf(
                            "gambling" to listOf("kazanmak", "kumar oynamak", "kırılgan", "etki"),
                            "frighten" to listOf("üzmek", "şaşırtmak", "korkutmak", "güldürmek"),
                            "generous" to listOf("cömert", "dikkatli", "dahi", "bencil"),
                            "delicate" to listOf("sevinç", "narin", "lezzetli", "yıkım"),
                            "fame" to listOf("alev", "ün", "hata", "özellik"),
                            "perfect" to listOf("tercih", "kisilik", "mükemmel", "kalıcı"),
                            "impatient" to listOf("hasta", "kusursuz", "sabırsız", "ilk"),
                            "indicate" to listOf("işaret etmek", "küçümsemek", "indüklemek", "bağımsız"),
                            "manner" to listOf("tutum", "huy", "ruh hali", "hissiyet"),
                            "precise" to listOf("yarım", "kesin", "tahmini", "belirsiz"),
                            "proposal" to listOf("teklif", "sıradışı", "çekimser", "karar"),
                            "rapid" to listOf("tepki", "anımsatmak", "hızlı", "sanmak"),
                            "remarkable" to listOf("muhtemelen", "dikkate değer", "sayısız", "ayrıcalıklı"),
                            "seek" to listOf("görmek", "aramak", "duymak", "ummak"),
                            "mere" to listOf("birleştirme", "yalnızca", "fazlasıyla", "yeterince"),
                            "enthusiastic" to listOf("coşkulu", "şaşıtıcı", "akıl almaz", "keskin"),
                            "emphasize" to listOf("abartmak", "indirgemek", "imrenmek", "vurgulamak"),
                            "destruction" to listOf("çarpma", "patlama", "yıkım", "batma"),
                            "awesome" to listOf("mükemmel", "dehşet", "inanılmaz", "kusursuz"),
                            "disaster" to listOf("imha", "acı", "olağandışı", "felaket"),
                            "contribute" to listOf("davet etmek", "katkı yapmak", "umut vermek", "borç vermek"),
                            "recall" to listOf("dönmek", "hatırlamak", "vazgecmek", "pişman olmak")
                        ), // Zor seviye cevapları
                        mapOf(
                            "gambling" to "kumar oynamak",
                            "frighten" to "korkutmak",
                            "generous" to "cömert",
                            "delicate" to "narin",
                            "fame" to "ün",
                            "perfect" to "mükemmel",
                            "impatient" to "sabırsız",
                            "indicate" to "işaret etmek",
                            "manner" to "tutum",
                            "precise" to "kesin",
                            "proposal" to "teklif",
                            "rapid" to "hızlı",
                            "remarkable" to "dikkate değer",
                            "seek" to "aramak",
                            "mere" to "yalnızca",
                            "enthusiastic" to "coşkulu",
                            "emphasize" to "vurgulamak",
                            "destruction" to "yıkım",
                            "awesome" to "dehşet",
                            "disaster" to "felaket",
                            "contribute" to "katkı yapmak",
                            "recall" to "hatırlamak"
                        ) // B2+ seviye doğru cevapları
                    )
                },
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp)
                    .padding(8.dp)
            ) {
                Text(text = "B2+", style = TextStyle(fontSize = 18.sp))
            }
        }
    }
}
