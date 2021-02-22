package guinea.diego.launchervideoinnovation.ui.detail

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.DetailsFragment
import androidx.leanback.widget.*
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import guinea.diego.launchervideoinnovation.BuildConfig
import guinea.diego.launchervideoinnovation.R
import guinea.diego.launchervideoinnovation.data.models.Proyectos
import guinea.diego.launchervideoinnovation.ui.detail.DetailActivity.Companion.KEY_VIDEO
import guinea.diego.launchervideoinnovation.ui.detail.DetailActivity.Companion.SHARED_ELEMENT_NAME
import guinea.diego.launchervideoinnovation.ui.playback.PlaybackActivity
import guinea.diego.launchervideoinnovation.ui.presenter.CardPresenter
import guinea.diego.launchervideoinnovation.ui.presenter.DetailOverviewLogoPresenter
import guinea.diego.launchervideoinnovation.ui.presenter.DetailsDescriptionPresenter
import guinea.diego.launchervideoinnovation.ui.webView.WebView
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.http.Url
import java.io.File
import java.lang.reflect.Array
import java.net.URL
import java.net.HttpURLConnection as HttpURLConnection
import kotlin.Array as Array1


class DetailFragment: DetailsFragment() {

    companion object {
        const val ACTION_WATCH_TRAILER = 1
    }

    private lateinit var backgroundManager: BackgroundManager
    private lateinit var metrics: DisplayMetrics
    private lateinit var proyectos: Proyectos
    private lateinit var mAdapter: ArrayObjectAdapter
    private  var mButton =  MutableLiveData<String>()
    private var defaultBackground: Drawable? = null
    private val objetos: ArrayList<Proyectos> = arrayListOf()
    private lateinit var tituloApk: String
    private lateinit var fullPath : String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareBackgroundManager()
        setupData()
        setupView()
        setupListener()
    }

    private fun prepareBackgroundManager() {

        backgroundManager = BackgroundManager.getInstance(activity)
        backgroundManager.attach(activity.window)
        defaultBackground = ContextCompat.getDrawable(activity, R.drawable.default_background)
        metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
    }

    private fun setupView() {
        permissionsInstall()
        setupAdapter()
        setupDetailsOverviewRow()
       // setupVideoListRow()
    }

    private fun setupListener() {
        onItemViewClickedListener = ItemViewClickedListener()
    }

    private fun setupAdapter() {
        val helper = FullWidthDetailsOverviewSharedElementHelper()
        helper.setSharedElementEnterTransition(activity, SHARED_ELEMENT_NAME)

        //Cambiar por un webview

        val detailsRowPresenter = FullWidthDetailsOverviewRowPresenter(
            DetailsDescriptionPresenter(),
            DetailOverviewLogoPresenter()
        )
        detailsRowPresenter.backgroundColor = ContextCompat.getColor(
            activity,
            R.color.colorNoTransparente
        ) //color del fondo
        detailsRowPresenter.initialState = FullWidthDetailsOverviewRowPresenter.STATE_HALF
        detailsRowPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()
        detailsRowPresenter.setListener(helper)
        detailsRowPresenter.setOnActionClickedListener {
            if (it.id.toInt() == ACTION_WATCH_TRAILER) {
                //BIFURCACION ENTRE CATEGORIAS
                if(proyectos.categoria == "Noticias"){
                    val intent = Intent(activity, WebView::class.java)
                    intent.putExtra("titulo", proyectos.titulo)
                    intent.putExtra("url", proyectos.accion)
                    startActivity(intent)
                }else if(proyectos.categoria == "Proyectos"){
                    comprobarPaquete()
                }else{
                    val intent = Intent(activity, PlaybackActivity::class.java)
                    intent.putExtra("videoUrl", proyectos.VideoEntero)
                    startActivity(intent)
                }

            } else {
                if (proyectos.categoria == "Proyectos"){
                  //  instalarApp()
                   // installAPK()
                }else{
                    Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        val presenterSelector = ClassPresenterSelector()
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsRowPresenter)
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        mAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = mAdapter
    }

    private fun progress(){

//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.activity_detail)
//        dialog.setTitle(proyectos.titulo)
//
//        dialog.show()
//
//        activity.downloadProgress.progress = 0F
        val prog = ProgressDialog(context)
        prog.setTitle(proyectos.titulo)
        prog.setMessage("Wait for Sometime")
        prog.max = 100
        prog.closeOptionsMenu()
        prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        prog.show()

        Thread(Runnable(){
            var i = 0
            while (i<=100){
                prog.progress = i
                i++
                if (i == 100){
                    prog.dismiss()
                }
                Thread.sleep(100)
            }
        }).start()

    }

    private fun comprobarPaquete() {
        val paquete = "com.govideo.livinappplayer"
        val pm = activity.applicationContext.packageManager
        val file = File(fullPath)

        val openApk = pm.getLaunchIntentForPackage(paquete)
        openApk?.addCategory(Intent.CATEGORY_LAUNCHER)
        openApk?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        openApk?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val launchIntent = activity.applicationContext.packageManager.getLaunchIntentForPackage("com.mobisystems.fileman")
        launchIntent?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            if (file.exists()) {
               // openApk.setDataAndType(, "application/vnd.android.package-archive");
                   launchIntent?.let {startActivity(launchIntent)}?: kotlin.run { Log.d("Prueba", "no existe") }
               //Intent.createChooser(openApk, "Open")
            } else{
                downloadFile()
                Handler().postDelayed({
                    installAPK()
                }, 8000)
            }
        } catch (ex: Exception) {
            Log.d("Prueba1", "${ex.localizedMessage}", ex)
            Toast.makeText(context, " No Application is found to open this file.", Toast.LENGTH_LONG).show()
        }
    }
    private fun permissionsInstall(){
        //installtion permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                        Uri.parse(
                            java.lang.String.format("package:%s", activity.packageName)
                        )
                    ), 1234
                )
            } else {
            }
        }

        //Storage Permission
        if (checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        }
        if (checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            )
        }
    }

    private fun downloadFile() {

        activity.viewDownload.visibility = View.VISIBLE
        progress()
       // activity.downloadAnimacion.visibility = View.VISIBLE

        val request = DownloadManager.Request(Uri.parse(proyectos.accion))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(proyectos.titulo)
        request.setDescription(proyectos.descripcion)

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        //request.setDestinationInExternalPublicDir(fullPath,"$tituloApk.apk")
        val file = File(fullPath)
        val uri = Uri.fromFile(file)
        request.setDestinationUri(uri)

        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        ///////

//        var downloadSize = 0
//        val url = URL(proyectos.accion)
//        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
//
//        urlConnection.requestMethod = "GET"
//        urlConnection.doOutput = true
//        urlConnection.connect()
//
//        val inputStream = urlConnection.inputStream
//        val totalSize = urlConnection.contentLength
//
//        Thread(Runnable(){
//            activity.downloadProgress.progressMax = totalSize.toFloat()
//        }).start()
//
//        val buffer = ByteArray(124)
//        var bufferLength = 0
//
//        while ((inputStream.read(buffer).also { bufferLength = it }) > 0) {
//            downloadSize += bufferLength
//            Thread(Runnable(){
//                activity.downloadProgress.progress = downloadSize.toFloat()
//            }).start()
//        }

        ////////


    }

    private fun installAPK() {
        val file = File(fullPath)

            if (file.exists()) {
                activity.viewDownload.visibility = View.INVISIBLE
               // activity.downloadAnimacion.visibility = View.INVISIBLE
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(
                    uriFromFile(
                        activity.applicationContext,
                        File(fullPath)
                    ), "application/vnd.android.package-archive"
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    activity.applicationContext.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Log.e("TAG", "Error in opening the file!")
                }
            } else {
                 Toast.makeText(
                     activity.applicationContext,
                     "Descaregando paquetes...",
                     Toast.LENGTH_LONG
                 ).show()
            }
    }

    private fun uriFromFile(context: Context?, file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context!!, BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
    }
    private fun setupDetailsOverviewRow() {
        val detailsOverviewRow = DetailsOverviewRow(proyectos)

        val options = RequestOptions()
                .error(R.drawable.background_title)
                .dontAnimate()
        //FOTO EN LA PANTALLA DE DETALLES//
//
//        Glide.with(this)
//                .asBitmap()
//                .load(video.foto)
//                .apply(options)
//                .into(object: SimpleTarget<Bitmap>() {
//                    override fun onResourceReady(
//                            resource: Bitmap,
//                            transition: Transition<in Bitmap>?) {
//                        detailsOverviewRow.setImageBitmap(activity, resource)
//                        startEntranceTransition()
//                    }
//                })

        val actionAdapter = SparseArrayObjectAdapter()
        actionAdapter.set(
            ACTION_WATCH_TRAILER,
            Action(
                ACTION_WATCH_TRAILER.toLong(),
                mButton.value,
                ""
            )
        )
        detailsOverviewRow.actionsAdapter = actionAdapter
        mAdapter.add(detailsOverviewRow)
    }

    private fun setupVideoListRow() {
        val header = HeaderItem(0, getString(R.string.related_videos))
        var cardRowAdapter = ArrayObjectAdapter(CardPresenter())
        objetos.forEach {
            if(!it.titulo.equals(proyectos.titulo)) {
                cardRowAdapter.add(it)
            }
        }
        mAdapter.add(ListRow(header, cardRowAdapter))
    }

    private fun setupData() {
        val id = activity.intent.getIntExtra("id", 0)
        val titulo = activity.intent.getStringExtra("titulo")
        val descripcion = activity.intent.getStringExtra("descripcion")
        val categoria = activity.intent.getStringExtra("categoria")
        val foto = activity.intent.getStringExtra("foto")
        val videoP = activity.intent.getStringExtra("videoP")
        val videoE = activity.intent.getStringExtra("videoE")
        val accion = activity.intent.getStringExtra("accion")



        proyectos = Proyectos(id, titulo, descripcion, categoria, videoP, videoE, foto, accion)
        tituloApk = proyectos.titulo?.replace("\\s".toRegex(), "").toString()
        fullPath =  activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/$tituloApk.apk"
        confgButton()
        proyectos.foto?.let { updateBackground(it) }
    }

    private fun confgButton() {
        if(proyectos.categoria == "Noticias"){
            mButton.value = "Ver Noticia"
        }else if (proyectos.categoria == "Videos"){
            mButton.value = "Reproducir"
        }else{
            val file = File(fullPath)
            if (file.exists()){
                mButton.value = "Abrir"
            }else{
                mButton.value = "Descargar"
            }

        }

    }

    private fun updateBackground(uri: String) {
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val options = RequestOptions()
                .centerCrop()
                .error(defaultBackground)

        Glide.with(this)
                .asBitmap()
                .load(uri)
                .apply(options)
                .into(object : SimpleTarget<Bitmap>(width, height) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        backgroundManager.setBitmap(resource)
                    }
                })
    }

    private inner class ItemViewClickedListener: OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if(item is Proyectos) {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra(KEY_VIDEO, item)
                startActivity(intent)
            }
        }
    }
}