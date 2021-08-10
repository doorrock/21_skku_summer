package com.cookandroid.realtest

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.xizzhu.simpletooltip.ToolTip
import com.github.xizzhu.simpletooltip.ToolTipView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.FileInputStream
import kotlin.text.StringBuilder

class ItemActivity : AppCompatActivity() {
    interface API2{
        @GET("/{product_name}")
        fun getSearch(@Path("product_name") product_name: String): Call<List<ResultGetSearch>>

    }

    var list = mutableListOf<ResultGetSearch>()
    var categoryName = arrayListOf("빵", "샌드위치", "샐러드",
        "아이스크림", "초콜릿", "사탕",
        "과자", "젤리", "시리얼",
        "탄산음료", "과/채음료", "커피",
        "라면", "김치", "유제품",
        "유산균", "잼", "소스")


    private var backPressedTime: Long = 0
    override fun onBackPressed() {
        // 2초내 다시 클릭하면 앱 종료
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            ActivityCompat.finishAffinity(this)
            return
        }

        // 처음 클릭 메시지
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }



    lateinit var pieChart : PieChart
    private lateinit var search_dialog : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_item)
        toolbar.title=""
        toolbar.inflateMenu(R.menu.menu_item)
        setSupportActionBar(toolbar)

        findViewById<FloatingActionButton>(R.id.fab2_item).setOnClickListener {
            val intent = Intent(this@ItemActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } //메인으로 floating button

        findViewById<FloatingActionButton>(R.id.fab_item).setOnClickListener {
            val intent = Intent(this@ItemActivity, CameraActivity::class.java)
            startActivity(intent)
            finish()
        } //카메라로 floating button


        var text_allergy = findViewById<TextView>(R.id.text_allergy_item)
        var inFs : FileInputStream = openFileInput("user_allergy.txt")
        var txt = ByteArray(13)
        inFs.read(txt)
        inFs.close()

        var tmp_allergy = arrayListOf("계란", "우유", "치즈", "밀가루", "참깨", "콩", "게", "새우", "조개", "복숭아", "오이", "마늘", "돼지고기")
        var user_info_allergy = txt.toString(Charsets.UTF_8).trim()
        var str_temp = StringBuilder()
        for(i in 0..user_info_allergy.length-1){
            if(user_info_allergy[i] == '1') str_temp.append(tmp_allergy[i] + " ")
        }
        text_allergy.text = str_temp.toString()

        var text_disease = findViewById<TextView>(R.id.text_disease_item)
        inFs = openFileInput("user_disease.txt")
        txt = ByteArray(8)
        inFs.read(txt)
        inFs.close()

        var tmp_disease = arrayListOf("나트륨", "탄수화물", "당류", "지방", "트랜스지방", "포화지방", "콜레스테롤", "단백질")
        var user_info_disease = txt.toString(Charsets.UTF_8).trim()
        str_temp = StringBuilder()
        for(i in 0..user_info_disease.length-1){
            if(user_info_disease[i] == '1') str_temp.append(tmp_disease[i] + " ")
        }
        text_disease.text = str_temp.toString()


        var intent = getIntent()
        var items : ArrayList<String> = intent.getStringArrayListExtra("data")
        var category_idx = intent.getIntExtra("category", 0)

        var grid_item = findViewById<ExpandableHeightGridView>(R.id.grid_item)
        var gridAdapter = gridAdapter(this@ItemActivity, items)
        grid_item.expanded = true
        grid_item.isFocusable = false
        grid_item.adapter = gridAdapter


        var test_text = findViewById<TextView>(R.id.text_item)
        test_text.text = makeResultText(items, user_info_allergy)

        initView()
        initPieChart()
        setPieChart(items)


        val product_name = categoryName[category_idx]
        val BASE_URL_API = "http://115.85.180.148:5000"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ResultActivity.API2::class.java)
        val callGetSearch = api.getSearch(product_name)

        callGetSearch.enqueue(object : Callback<List<ResultActivity.ResultGetSearch>> {
            override fun onResponse(
                call: Call<List<ResultActivity.ResultGetSearch>>,
                response: Response<List<ResultActivity.ResultGetSearch>>
            ) {

                Log.d(ContentValues.TAG, "성공 : ${response.raw()}")
                var data : List<ResultActivity.ResultGetSearch>? = response?.body()
                if(list.isEmpty()){
                    for ( i in data!!) {
                        i.let {
                            val title = it.title
                            val content = it.content
                            val imageurl= it.imageurl
                            list.add(ResultGetSearch(title, content, imageurl))

                            Log.i("data", i.toString())

                        }

                    }
                }


            }

            override fun onFailure(call: Call<List<ResultActivity.ResultGetSearch>>, t: Throwable) {
                Log.d(ContentValues.TAG, "실패 : $t")
            }
        })
    }

    private fun makeResultText(data:ArrayList<String>, user_info: String): String {
        var allergy_list = arrayOf("복숭아", "아몬드", "계란", "갑각류", "생선", "대파", "밀", "쌀", "설탕", "콩", "소금", "마늘", "돼지고기")
        var whtk = arrayOf("가", "가", "이", "가", "이", "가", "이", "이", "이", "이", "이", "이", "가")
        var sb = StringBuilder()
        var flag = false
        // 포함된거 쭉 보여주고, 포함안된거 쭉 보여주기
        for(i in 0..allergy_list.size-1){
            if(user_info[i] == '1'){
                if(data.contains(allergy_list[i])){
                    sb.append("\n이 제품에는 ${allergy_list[i]}${whtk[i]} 포함되어 있습니다.\n")
                    sb.append("${allergy_list[i]} 알레르기에 주의하세요!\n")
                    flag = true
                }
            }
        }
        if(!flag) sb.append("관심있는 알레르기 성분이 포함되어 있지 않습니다.")

        return sb.toString()
    }

    private fun initView() {
        pieChart = findViewById(R.id.pie_chart_item)
    }

    private fun initPieChart() {
        pieChart.run{
            description.isEnabled = false
            legend.isEnabled = false
            setUsePercentValues(false)
            isDrawHoleEnabled = true
            isRotationEnabled = false

            setEntryLabelColor(ContextCompat.getColor(this@ItemActivity, R.color.black))
            animateY(800)
        }
    }


    private fun setPieChart(items_data: ArrayList<String>) {
        val colorList = ArrayList<Int>()
        for(c in ColorTemplate.PASTEL_COLORS) colorList.add(c)
        for(c in ColorTemplate.VORDIPLOM_COLORS) colorList.add(c)
        for(c in ColorTemplate.LIBERTY_COLORS) colorList.add(c)

        val temp = 100f / items_data.size
        val entries = ArrayList<PieEntry>()
        for(i in 0..items_data.size-1) {
            val item = items_data[i]
            entries.add(PieEntry(temp,item))
        }

        val d = PieDataSet(entries, "DataSet")
            .apply {
                colors = colorList
                sliceSpace = 3f
                selectionShift = 5f
                valueTextSize = 0f
            }

        val pieData = PieData(d)
        pieChart.data = pieData
        if(items_data.size > 20) pieChart.setEntryLabelTextSize(8f)
    }




    class gridAdapter(var context: Context, var item_data : ArrayList<String>) : BaseAdapter(){
        var explain = mutableMapOf(
            "구연산삼나트륨" to "청량음료수의 산미 완화제, 가공식품, 잼, 젤리, 사탕 등의 pH 조정제, 유제품의 산패방지제, 치즈와 어육연제품의 증점제, 아이스크림의 유화 및 안정제",
            "효소처리스테비아" to "감미도가 설탕의 약 100~200배인 감미료",
            "글루탐산나트륨" to "일명 MSG. 동양 요리에 많이 사용되는 식품 첨가물로 다시마에서 추출한 물질을 원료로 만든다",
            "니코틴산아미드" to "니코틴산의 아마이드로서 수용성 비타민과 비타민 B 복합체의 하나이다",
            "토코페롤" to "지방에 녹고 산화 방지 활성이 있는 비타민 이(E)",
            "대두" to "‘밭의 고기’라고 불릴 정도로 양질의 식물성 단백질과 지방이 풍부한 대두는 우리가 매일 접하는 간장, 된장의 원료로 이용되고 두부, 콩나물과 같은 필수 식품의 원천이다",
            "팽창제" to "제과나 제빵시 조직을 연하게 하고 기호성을 높이기 위해서 첨가하는 것"
        )

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var btn = Button(context)
            btn.setBackgroundResource(R.drawable.custom_button)
            btn.text = item_data[p0]
            btn.setOnClickListener {
                var sb = StringBuilder()
                if(explain.contains(item_data[p0])) sb.append(explain[item_data[p0]])
                else sb.append("No data")
                var tooltip = ToolTip.Builder()
                    .withTextSize(40f)
                    .withText(sb.toString())
                    .build()
                var tooltipView = ToolTipView.Builder(context)
                    .withAnchor(btn)
                    .withToolTip(tooltip)
                    .withGravity(Gravity.BOTTOM)
                    .build()
                tooltipView.show()
            }
            return btn
        }

        override fun getItem(p0: Int): Any {
            return 0
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return item_data.size
        }
    }



    data class ResultGetSearch(
        @SerializedName("title") val title: String,
        @Expose
        @SerializedName("content") val content: String,
        @SerializedName("imageurl") val imageurl: String
    )


    inner class MyListAdapter(var mCtx:Context, var resource:Int, var items:List<ResultGetSearch>)
        :ArrayAdapter<ResultGetSearch>( mCtx , resource , items ){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
            val view : View = layoutInflater.inflate(resource , null )
            val imageView :ImageView = view.findViewById(R.id.iconIv)
            var textView : TextView = view.findViewById(R.id.titleTv)
            var textView1 : TextView = view.findViewById(R.id.descTv)


            var person : ResultGetSearch = items[position]

            val url = person.imageurl
            Glide.with(this@ItemActivity).load(url).into(imageView)
            textView.text = person.title
            textView1.text = person.content


            return view
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                search_dialog = View.inflate(this@ItemActivity, R.layout.dialog_main, null) //dialog_main.xml 대입
                var listView_search = search_dialog.findViewById<ListView>(R.id.listView_search)
                var adapter_search = MyListAdapter(this, R.layout.row, list)
                adapter_search.notifyDataSetChanged()
                listView_search.adapter = adapter_search

                val dlg = AlertDialog.Builder(this@ItemActivity)
                dlg.setTitle("관련 상품 목록")
                dlg.setView(search_dialog)
                dlg.setPositiveButton("닫기", null)
                dlg.show()
                return true
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }
        }
    }

}