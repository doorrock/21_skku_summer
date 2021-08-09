package com.cookandroid.realtest

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder


class CameraActivity : AppCompatActivity() {

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var verType: Boolean = true
    lateinit var dialogView: View

    override fun onPause() {
        super.onPause()
        mCamera?.release()
        mCamera = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val toolbar=findViewById<Toolbar>(R.id.toolbar_camera)
        toolbar.title = "  CAMERA"
        toolbar.inflateMenu(R.menu.menu_camera)
        setSupportActionBar(toolbar)

        // Create an instance of Camera
        mCamera = getCameraInstance()
        val params: Camera.Parameters? = mCamera?.parameters
        val focusModes: List<String>? = params?.supportedFocusModes
        if (focusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) == true) {
            params?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            mCamera?.parameters = params
        }

        mPreview = mCamera?.let {
            // Create our Preview view
            CameraPreview(this, it)
        }


        // Set the Preview view as the content of our activity.
        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
            preview.post{
                preview.addView(MyGraphicView(this, preview.width, preview.height, verType))
            }
            preview.setOnClickListener {
                mCamera?.autoFocus{ success, camera ->
                    if(success) Toast.makeText(applicationContext, "FOCUSING", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(applicationContext, "NOPE", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val homeButton : ImageButton = findViewById(R.id.button_home)
        homeButton.setOnClickListener {
            var intent = Intent(this@CameraActivity, MainActivity::class.java)
            startActivity(intent)
            mCamera?.release()
            mCamera = null
        }

        val rotateButton: ImageButton = findViewById(R.id.button_rotate)
        rotateButton.setOnClickListener {
            // 화면 회전
            verType = !verType
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.removeViewAt(1)
            preview.addView(MyGraphicView(this, preview.width, preview.height, verType))
        }

        val captureButton: ImageButton = findViewById(R.id.button_capture)
        captureButton.setOnClickListener {
            // get an image from the camera
            mCamera?.takePicture(null, null, mPicture)
        }

    }


    //카메라 객체 가져오기
    fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }
    }

    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile: File = getOutputMediaFile() ?: run {
            Log.d(TAG, ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        try {
            Log.i("MyCamApp", "$pictureFile")
            val fos = FileOutputStream(pictureFile)
            Log.i("MyCamApp", "${data.size}")
            Log.i("MyCamApp", "${mPreview!!.width}")
            Log.i("MyCamApp", "${mPreview!!.height}")

            var options = BitmapFactory.Options()
            options.inSampleSize = 2
            var tmpImg : Bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)
            tmpImg = rotate(tmpImg, 90f)

            if(verType == true) {
                val w1: Int = tmpImg.width / 10 * 2
                val w2: Int = tmpImg.width / 10 * 8
                val h1: Int = tmpImg.height / 10 * 2
                val h2: Int = tmpImg.height / 10 * 8
                tmpImg = Bitmap.createBitmap(tmpImg, w1, h1, w2 - w1, h2 - h1)
            }
            else{
                val w3 : Int = tmpImg.width / 10 * 1
                val w4 : Int = tmpImg.width / 10 * 9
                val h3 : Int = tmpImg.height / 10 * 3
                val h4 : Int = tmpImg.height / 10 * 7
                tmpImg = Bitmap.createBitmap(tmpImg, w3, h3, w4 - w3, h4 - h3)
            }

            tmpImg = resizeBitmapImage(tmpImg, 1024)
            tmpImg.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$pictureFile")))

            mCamera?.release()
            mCamera = null
            choose_category()

        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        }
    }

    private fun rotate(bitmap:Bitmap, degree:Float) : Bitmap{
        var width = bitmap.width
        var height = bitmap.height

        var mtx = Matrix()
        mtx.setRotate(degree)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true)
    }

    private fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        var rate = 0.0f

        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / width.toFloat()
                newHeight = (height * rate).toInt()
                newWidth = maxResolution
            }
        }
        else {
            if (maxResolution < height) {
                rate = maxResolution / height.toFloat()
                newWidth = (width * rate).toInt()
                newHeight = maxResolution
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }

    //
    private fun getOutputMediaFile(): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCamApp"
        )
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCamApp", "failed to create directory")
                    return null
                }
            }
        }

        // Create a media file name
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File("${mediaStorageDir.path}${File.separator}test.jpg")
//        return File("${mediaStorageDir.path}${File.separator}test.jpg")
    }


    /** A basic Camera preview class */
    class CameraPreview(
        context: Context,
        private val mCamera: Camera
    ) : SurfaceView(context), SurfaceHolder.Callback {

        private val mHolder: SurfaceHolder = holder.apply {
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            addCallback(this@CameraPreview)
            // deprecated setting, but required on Android versions prior to 3.0
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            mCamera.apply {
                try {
                    setDisplayOrientation(90)
                    setPreviewDisplay(holder)
                    startPreview()
                } catch (e: IOException) {
                    Log.d(TAG, "Error setting camera preview: ${e.message}")
                }
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.
            if (mHolder.surface == null) {
                // preview surface does not exist
                return
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview()
            } catch (e: Exception) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            mCamera.apply {
                try {
                    setPreviewDisplay(mHolder)
                    startPreview()
                } catch (e: Exception) {
                    Log.d(TAG, "Error starting camera preview: ${e.message}")
                }
            }
        }
    }

    private class MyGraphicView(context: Context, w : Int, h : Int, t: Boolean) : View(context){

        val w1 : Float = (w / 10 * 2).toFloat()
        val w2 : Float = (w / 10 * 8).toFloat()
        val w3 : Float = (w / 10 * 1).toFloat()
        val w4 : Float = (w / 10 * 9).toFloat()
        val h1 : Float = (h / 10 * 2).toFloat()
        val h2 : Float = (h / 10 * 8).toFloat()
        val h3 : Float = (h / 10 * 3).toFloat()
        val h4 : Float = (h / 10 * 7).toFloat()

        val type: Boolean = t

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)

            val pt = Paint()
            pt.style = Paint.Style.STROKE
            pt.setColor(Color.GREEN)
            pt.strokeWidth = 10f

            if(type == true) {
                canvas!!.drawLine(w1 - 5f, h1, w1 + 100f, h1, pt)
                canvas!!.drawLine(w1, h1, w1, h1 + 100f, pt)

                canvas!!.drawLine(w2 - 100f, h1, w2 + 5f, h1, pt)
                canvas!!.drawLine(w2, h1, w2, h1 + 100f, pt)

                canvas!!.drawLine(w1 - 5f, h2, w1 + 100f, h2, pt)
                canvas!!.drawLine(w1, h2 - 100f, w1, h2, pt)

                canvas!!.drawLine(w2 - 100f, h2, w2 + 5f, h2, pt)
                canvas!!.drawLine(w2, h2 - 100f, w2, h2, pt)
            }
            else{
                canvas!!.drawLine(w3 - 5f, h3, w3 + 100f, h3, pt)
                canvas!!.drawLine(w3, h3, w3, h3 + 100f, pt)

                canvas!!.drawLine(w4 - 100f, h3, w4 + 5f, h3, pt)
                canvas!!.drawLine(w4, h3, w4, h3 + 100f, pt)

                canvas!!.drawLine(w3 - 5f, h4, w3 + 100f, h4, pt)
                canvas!!.drawLine(w3, h4 - 100f, w3, h4, pt)

                canvas!!.drawLine(w4 - 100f, h4, w4 + 5f, h4, pt)
                canvas!!.drawLine(w4, h4 - 100f, w4, h4, pt)
            }
        }
    }
    private class categoryAdapter(var context: Context, var edit_gram: EditText): BaseAdapter(){
        var categoryName = arrayListOf("빵", "샌드위치", "샐러드",
                                       "아이스크림", "초콜릿", "사탕",
                                       "과자", "젤리", "시리얼",
                                       "탄산음료", "과/채음료", "커피",
                                       "라면", "김치", "유제품",
                                       "유산균", "잼", "소스")

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var btn = Button(context)
            btn.text = categoryName[p0]
            btn.setBackgroundResource(R.drawable.custom_button)

            btn.setOnClickListener {
                var intent = Intent(context, OcrActivity::class.java)
                var gram = edit_gram.text.toString().toInt()
                intent.putExtra("gram", gram)
                intent.putExtra("category", p0)

                context.startActivity(intent)
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
            return categoryName.size
        }
    }

    private fun choose_category(){

        dialogView = View.inflate(this@CameraActivity, R.layout.choose_category, null)
        var edit_gram = dialogView.findViewById<EditText>(R.id.edit_gram)

        var gridTest = dialogView.findViewById<GridView>(R.id.gridTest)
        var adapter = categoryAdapter(this@CameraActivity, edit_gram)
        gridTest.adapter = adapter

        var dlg = AlertDialog.Builder(this@CameraActivity)
        dlg.setTitle("카테고리 선택")
        dlg.setIcon(R.drawable.ic_action_person_black)
        dlg.setView(dialogView)

        dlg.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_camera, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.user_information -> {
                dialogView = View.inflate(this@CameraActivity, R.layout.dialog_user, null)

                var tempA = arrayOf("복숭아", "아몬드", "계란", "갑각류", "생선", "대파", "밀", "쌀", "설탕", "콩", "소금", "마늘", "돼지고기")
                var tempA_value = ArrayList<String>()
                for(i in 0..tempA.size-1) tempA_value.add(tempA[i])

//                var tempB = arrayOf("열량", "나트륨", "탄수화물", "당류", "식이섬유", "지방", "트랜스지방", "포화지방", "콜레스테롤", "단백질")
                var tempB = arrayOf("나트륨", "탄수화물", "당류", "지방", "트랜스지방", "포화지방", "콜레스테롤", "단백질")
                var tempB_value = ArrayList<String>()
                for(i in 0..tempB.size-1) tempB_value.add(tempB[i])

                var listView_allergy = dialogView.findViewById<ListView>(R.id.listView_allergy)
                var listView_disease = dialogView.findViewById<ListView>(R.id.listView_disease)


                // allergy 정보 리스트뷰에 넣기
                var adapter_allergy : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, tempA_value)
                listView_allergy.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                listView_allergy.adapter = adapter_allergy

                try{
                    var data_tempA = ByteArray(20)
                    var inFs : FileInputStream = openFileInput("user_allergy.txt")
                    inFs.read(data_tempA)
                    inFs.close()

                    var str_temp = data_tempA.toString(Charsets.UTF_8).trim()
                    for(i in 0..str_temp.length-1){
                        if(str_temp[i] == '1') listView_allergy.setItemChecked(i, true)
                    }
                }
                catch (e: java.lang.Exception){
                    Toast.makeText(applicationContext, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                }

                listView_allergy.setOnItemClickListener { parent, view, position, id ->
                    var inFs : FileInputStream
                    var user_data = ByteArray(20)
                    try{
                        inFs = openFileInput("user_allergy.txt")
                        inFs.read(user_data)
                        inFs.close()
                    }
                    catch(e: IOException){
                        var outFs : FileOutputStream = openFileOutput("user_allergy.txt", Context.MODE_PRIVATE)
                        user_data = "000000000".toByteArray()
                        outFs.close()
                    }

                    var str = StringBuilder(user_data.toString(Charsets.UTF_8).trim())
                    if(str[position] == '1') str[position] = '0'
                    else str[position] = '1'

                    Log.i("test", "str : $str")
                    Log.i("test", "pos : $position")

                    Toast.makeText(applicationContext, str.toString(), Toast.LENGTH_SHORT).show()

                    var outFs : FileOutputStream = openFileOutput("user_allergy.txt", Context.MODE_PRIVATE)
                    outFs.write(str.toString().toByteArray())
                    outFs.close()
                }

                var adapter_disease: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, tempB_value)
                listView_disease.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                listView_disease.adapter = adapter_disease

                try{
                    var data_tempB = ByteArray(10)
                    var inFs : FileInputStream = openFileInput("user_disease.txt")
                    inFs.read(data_tempB)
                    inFs.close()

                    var str_temp = data_tempB.toString(Charsets.UTF_8).trim()
                    for(i in 0..str_temp.length-1){
                        if(str_temp[i] == '1') listView_disease.setItemChecked(i, true)
                    }
                }
                catch (e: java.lang.Exception){
                    Toast.makeText(applicationContext, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                }

                listView_disease.setOnItemClickListener { parent, view, position, id ->
                    var inFs : FileInputStream
                    var user_data = ByteArray(10)
                    try{
                        inFs = openFileInput("user_disease.txt")
                        inFs.read(user_data)
                        inFs.close()
                    }
                    catch(e: IOException){
                        var outFs : FileOutputStream = openFileOutput("user_disease.txt", Context.MODE_PRIVATE)
                        user_data = "00000000".toByteArray()
                        outFs.close()
                    }

                    var str = StringBuilder(user_data.toString(Charsets.UTF_8).trim())
                    if(str[position] == '1') str[position] = '0'
                    else str[position] = '1'

                    Log.i("test", "str : $str")
                    Log.i("test", "pos : $position")

                    Toast.makeText(applicationContext, str.toString(), Toast.LENGTH_SHORT).show()

                    var outFs : FileOutputStream = openFileOutput("user_disease.txt", Context.MODE_PRIVATE)
                    outFs.write(str.toString().toByteArray())
                    outFs.close()
                }


                val dlg = AlertDialog.Builder(this@CameraActivity)
                dlg.setTitle("사용자 정보 입력")
                dlg.setIcon(R.drawable.ic_action_person_black)
                dlg.setView(dialogView)
                dlg.setPositiveButton("확인", null)
                dlg.show()
                return true
            }

            R.id.question -> {
                dialogView = View.inflate(this@CameraActivity, R.layout.dialog_using_camera, null)
                var dlg = AlertDialog.Builder(this@CameraActivity)
                dlg.setView(dialogView)
                dlg.setTitle(" 카메라 사용방법")
                dlg.setIcon(R.drawable.questionb)
                dlg.setPositiveButton("확인", null)
                dlg.show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

}