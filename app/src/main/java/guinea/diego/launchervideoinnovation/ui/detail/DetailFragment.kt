package guinea.diego.launchervideoinnovation.ui.detail

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
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
import guinea.diego.launchervideoinnovation.ui.home.HomeActivity
import guinea.diego.launchervideoinnovation.ui.playback.PlaybackActivity
import guinea.diego.launchervideoinnovation.ui.presenter.CardPresenter
import guinea.diego.launchervideoinnovation.ui.presenter.DetailOverviewLogoPresenter
import guinea.diego.launchervideoinnovation.ui.presenter.DetailsDescriptionPresenter
import guinea.diego.launchervideoinnovation.ui.webView.WebView
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.internal.wait
import java.io.File


class DetailFragment: DetailsFragment() {

    companion object {
        const val ACTION_WATCH_TRAILER = 1
        const val ACTION_RENT = 2
    }
//    private var downloadBar = activity.findViewById<ProgressBar>(R.id.progressBar)
    private lateinit var backgroundManager: BackgroundManager
    private lateinit var metrics: DisplayMetrics
    private lateinit var proyectos: Proyectos
    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var mButton: String
    private lateinit var install: String
    private var defaultBackground: Drawable? = null
    private val objetos: ArrayList<Proyectos> = arrayListOf()
    private lateinit var tituloApk: String
    private lateinit var fullPath : String
    private var loadingDialog: Dialog? = null
    private val installMLD = MutableLiveData<Boolean>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareBackgroundManager()
        setupData()
        setupView()
        setupListener()
    }

//    private fun showDialog() {
//        hideLoading()
//        loadingDialog = activity?.showLoadingDialog()
//    }
//    private fun hideLoading() {
//        loadingDialog?.let { if (it.isShowing) it.cancel() }
//    }
//    private fun stopAnimacion() {
//        Handler().postDelayed({
//            hideLoading()
//        }, 1)
//    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity)
        backgroundManager.attach(activity.window)
        defaultBackground = ContextCompat.getDrawable(activity, R.drawable.default_background)
        metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
    }

    private fun setupView() {
//        if(checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//            PackageManager.PERMISSION_DENIED ){
//            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
//            //requestPermissions(arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES), 1000)
//        }
        permissionsInstall()
        setupAdapter()
        setupDetailsOverviewRow()
        setupVideoListRow()
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
                    downloadFile()
                    Handler().postDelayed({
                        installAPK()
                    }, 5000)

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

    private fun comprobarPaquete(): Boolean {
        val file = File(fullPath)
        return file.exists()
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
        activity.downloadAnimacion.visibility = View.VISIBLE
        //showDialog()
      // activity.downloadBar.visibility = View.VISIBLE

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

        //installAPK()
       // Toast.makeText(activity, "Download $uri", Toast.LENGTH_LONG).show()
    }

    private fun installAPK() {

        val file = File(fullPath)

        //installMLD.observe( HomeActivity().detail_fragment.viewLifecycleOwner, Observer {
           // Toast.makeText(activity.applicationContext,"entro dentro", Toast.LENGTH_LONG).show()
            if (file.exists()) {
                activity.downloadAnimacion.visibility = View.INVISIBLE
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
                 Toast.makeText(activity.applicationContext,"Descaregando paquetes...", Toast.LENGTH_LONG).show()
            }
       // })
       // Toast.makeText(activity.applicationContext,"Noooo entro", Toast.LENGTH_LONG).show()
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
                mButton,
                ""
            )
        )
//        if (proyectos.categoria == "Proyectos"){
//            actionAdapter.set(
//                ACTION_RENT,
//                Action(
//                    ACTION_RENT.toLong(),
//                    "Instalar",
//                    ""
//                )
//            )
//        }
//        actionAdapter.set(ACTION_BUY,
//                Action(ACTION_BUY.toLong(),
//                        getString(R.string.buy_1),
//                        getString(R.string.buy_2)))
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
        installMLD.value = comprobarPaquete()
        confgButton()
        proyectos.foto?.let { updateBackground(it) }
    }

    private fun confgButton() {
        if(proyectos.categoria == "Noticias"){
            mButton = "Ver Noticia"
        }else if (proyectos.categoria == "Videos"){
            mButton = "Reproducir"
        }else{
            mButton = "Descargar"
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