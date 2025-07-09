package com.example.projectwork_1

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.versionedparcelable.ParcelField
import androidx.versionedparcelable.VersionedParcelize
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.parcel.Parcelize

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initNavigation()
        //animStart()
        //objAnimStart()
        mainRecycler()
    }

    fun initNavigation() {

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val bottom_view = findViewById<BottomNavigationView>(R.id.bottom_menu)

        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "There will be navigation here someday", Toast.LENGTH_SHORT).show()
        }

        bottom_view.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.favorites -> {
                    Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.watch_later -> {
                    Toast.makeText(this, "Watch later", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.collections -> {
                    Toast.makeText(this, "Collections", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

    }


    fun mainRecycler() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = FilmListAdapter(object : FilmListAdapter.OnItemClickListener {
            override fun click(film: Film) {
                val bundle = Bundle()
                bundle.putParcelable("film", film)
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val decorator = FilmListItemDecor(8)
        recyclerView.addItemDecoration(decorator)
        adapter.addItems(filmDataBase)

        //Для теста работы DiffUtil
//        val diffTest = mutableListOf<Film>()
//        diffTest.addAll(filmDataBase)
//        diffTest.add(Film("Hulk", R.drawable.incredible_hulk, "Он большой. Он сильный. Он вспыльчив. И своей славой он обязан сомнительным веществам, повышающим работоспособность." +
//                " Нет, нет, я не говорю об определенном бейсболисте " +
//                "высшей лиги. Я имею в виду Халка - зеленое чудовище ростом 9 футов, которое рычит \"Hulk smash!\"" +
//                " и воспринимает это как обещание."))
//        val bottomMenu = findViewById<BottomNavigationView>(R.id.bottom_menu)
//        bottomMenu.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.collections -> adapter.addItems(diffTest)
//            }
//            false
//        }
    }
    //Вынес код анимаций в отдельный метод. ViewPropertyAnimation это как второй способ реализации анимаций.
//    fun animStart() {
//        val poster1Anim = findViewById<CardView>(R.id.poster_1)
//        val poster2Anim = findViewById<CardView>(R.id.poster_2)
//        val poster3Anim = findViewById<CardView>(R.id.poster_3)
//        val poster4Anim = findViewById<CardView>(R.id.poster_4)
//
//        poster1Anim.scaleX = 0f
//        poster1Anim.scaleY = 0f
//        poster1Anim.alpha = 0f
//
//        poster2Anim.scaleX = 0f
//        poster2Anim.scaleY = 0f
//        poster2Anim.alpha = 0f
//
//        poster3Anim.scaleX = 0f
//        poster3Anim.scaleY = 0f
//        poster3Anim.alpha = 0f
//
//        poster4Anim.scaleX = 0f
//        poster4Anim.scaleY = 0f
//        poster4Anim.alpha = 0f
//
//        poster1Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//
//        poster2Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//
//        poster3Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//
//        poster4Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//    }

    //Третий способ через ObjectAnimator/ValueAnimator
//    fun objAnimStart() {
//
//        val poster1Anim = findViewById<CardView>(R.id.poster_1)
//        val poster2Anim = findViewById<CardView>(R.id.poster_2)
//        val poster3Anim = findViewById<CardView>(R.id.poster_3)
//        val poster4Anim = findViewById<CardView>(R.id.poster_4)
//
//        val anim1X = ObjectAnimator.ofFloat(poster1Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim1Y = ObjectAnimator.ofFloat(poster1Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim1A = ObjectAnimator.ofFloat(poster1Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val anim2X = ObjectAnimator.ofFloat(poster2Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim2Y = ObjectAnimator.ofFloat(poster2Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim2A = ObjectAnimator.ofFloat(poster2Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val anim3X = ObjectAnimator.ofFloat(poster3Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim3Y = ObjectAnimator.ofFloat(poster3Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim3A = ObjectAnimator.ofFloat(poster3Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val anim4X = ObjectAnimator.ofFloat(poster4Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim4Y = ObjectAnimator.ofFloat(poster4Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim4A = ObjectAnimator.ofFloat(poster4Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val animatorSet = AnimatorSet ()
//        animatorSet.playTogether(anim1X, anim1Y, anim1A, anim2X, anim2Y, anim2A, anim3X, anim3Y, anim3A, anim4X, anim4Y, anim4A)
//        animatorSet.start()
//    }

    //module24 Создание RecyclerView

    private lateinit var filmsAdapter: FilmListAdapter

    @Parcelize
    data class Film(
        val title: String,
        val poster: Int,
        val description: String
    ) : Parcelable

    val filmDataBase = mutableListOf<Film>(
        Film(
            "Fight Club",
            R.drawable.fightclub,
            "Этот фильм рассказывает о молодом человеке, которому надоела его скучная жизнь. Он работает в одной корпорации рядовым клерком. Молодой человек страдает от бессонницы, он даже иногда путает, где реальность, а где может быть сон. Самое развлекательное действие у него, это листание каталога и выбирание себе в дом мебели. После очередной командировки, возвращаясь, домой, он знакомится в самолете с Тайлером Дерденом – торговцем и изготовителем мыла."
        ),
        Film(
            "Scar Face",
            R.drawable.liczo,
            "Это Майами в начале восьмидесятых, и бандит Тони Монтана (Аль Пачино) только что прибыл сюда вместе с тысячами других кубинцев, наводнивших Флориду, когда Фидель Кастро опустошил свои тюрьмы. После жестокого знакомства с кокаиновым бизнесом безжалостные амбиции Тони заставляют его очень быстро подняться по карьерной лестнице"
        ),
        Film(
            "Terminator",
            R.drawable.terminator,
            "«Терминатор» – культовый фантастический боевик с элементами триллера, который вышел на экраны в 1984 году. В центре сюжета – неумолимый киборг (Арнольд Шварценеггер), посланный из постапокалиптического будущего с миссией уничтожить Сару Коннор (Линда Хэмилтон), чьё будущее дитя предстоит возглавить сопротивление против машин."
        ),
        Film(
            "Rocky",
            R.drawable.rocky,
            "Заголовок: «Рокки» Описание: «Рокки» - знаковая спортивная драма 1976 года, рассказывающая о неудачливом боксере из Филадельфии, Рокки Бальбоа. Главный герой, чья роль прославила Сильвестра Сталлоне, получает шанс изменить свою жизнь, выйдя на ринг против чемпиона мира в тяжелом весе."
        ),
        Film(
            "Jaws",
            R.drawable.jaws,
            "«Челюсти» – культовый триллер 1975 года, снятый режиссером Стивеном Спилбергом, ставший эталоном жанра ужасов и приключений. Фильм повествует о маленьком островном сообществе, оказавшемся в панике после серии смертоносных нападений огромной белой акулы на людей."
        ),
        Film(
            "Three Bogatyrs",
            R.drawable.tri_bogatirya,
            "Неуязвимый и говорящий конь по имени Юлий постоянно оказывается в передрягах. Он всегда старается быть тихим, а также спокойным, но это ему не удается. Несмотря на это, персонаж мультфильма «Три богатыря и конь на троне» только и делает, что ищет множество новых проблем, вовлекая в проблемы всех, кто его окружает."
        ),
        Film(
            "Tamby Masaev",
            R.drawable.tambi,
            "Фильм раскрывает тайну происхождения Тамби, раскрывая его настоящую сущность и назначение. В картине зрителям предстоит узнать, откуда пришёл этот персонаж и какую задачу он должен выполнить. Эта история позволит лучше понять мотивы его поступков и детали прошлого, которые были ранее неизвестны. Фильм обещает быть полным открытий касательно его характера и предназначения в общей сюжетной линии."
        ),
        Film(
            "Friday",
            R.drawable.friday,
            "Чудесная американская комедия о двух темнокожих парнях. События происходят на территории Южного квартала, где проживают преимущественно темнокожие. Здесь для таких людей нет высоких гарантий и перспектив для развития успешной карьеры с соблюдением закона."
        ),
        Film(
            "Hulk",
            R.drawable.incredible_hulk,
            "Он большой. Он сильный. Он вспыльчив. И своей славой он обязан сомнительным веществам, повышающим работоспособность. Нет, нет, я не говорю об определенном бейсболисте высшей лиги. Я имею в виду Халка - зеленое чудовище ростом 9 футов, которое рычит \"Hulk smash!\" и воспринимает это как обещание."
        ),
        Film(
            "Fast and Furious",
            R.drawable.trojnoj_forsazh,
            "После того как Шон разбивает стройку во время автомобильных гонок, судья предлагает ему выбор: Школа для несовершеннолетних или переехать жить к отцу в Японию. И вот он в Токио, надевает свою симпатичную школьную форму и меняет туфли на тапочки, прежде чем войти в класс, где он не читает, не пишет и не понимает ни слова по-японски. Говорят, что можно учиться через полное погружение."
        )
    )
}

class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.title)
    private val poster: ImageView = itemView.findViewById(R.id.poster)
    private val description: TextView = itemView.findViewById(R.id.description)

    fun bind(film: MainActivity.Film) {
        title.text = film.title
        poster.setImageResource(film.poster)
        description.text = film.description
    }
}

class FilmListAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = mutableListOf<MainActivity.Film>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is FilmViewHolder -> {
                holder.bind(items[position])
                holder.itemView.setOnClickListener { clickListener.click(items[position]) }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(list: MutableList<MainActivity.Film>) {
        //DiffUtil из дополнительного задания реализован
        val oldData = items
        val newData = list.toMutableList()
        val diff = FilmDiffUtil(oldData, newData)
        val diffRes = DiffUtil.calculateDiff(diff)
        items = newData
        diffRes.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun click(film: MainActivity.Film)
    }
}

class FilmDiffUtil(val oldList: List<MainActivity.Film>, val newList: List<MainActivity.Film>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].poster == newList[newItemPosition].poster &&
                oldList[oldItemPosition].description == newList[newItemPosition].description
    }
}

class FilmListItemDecor(private val paddingInDp: Int) : RecyclerView.ItemDecoration() {
    private val Int.convertPx: Int
        get() {
            return this * Resources.getSystem().displayMetrics.density.toInt()
        }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = paddingInDp.convertPx
        outRect.right = paddingInDp.convertPx
        outRect.left = paddingInDp.convertPx
    }
}