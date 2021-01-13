package guinea.diego.launchervideoinnovation.ui.playback

import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.Observer
import guinea.diego.launchervideoinnovation.data.models.Proyectos
import org.koin.android.ext.android.inject


class PlaybackFragment: VideoSupportFragment() {

    private lateinit var transportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>
    private val viewModel by inject<PlaybackViewModel>()
    private val objetos: ArrayList<Proyectos> = arrayListOf()
    private lateinit var video: Proyectos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllData()
        ObserverMLD()
    }

    private fun ObserverMLD() {
        viewModel.valuesViewMLD.observe( this , Observer {
            objetos.addAll(it.proyectos)
            setupData()
            setupView()
        })
    }

    override fun onPause() {
        super.onPause()
        transportControlGlue.pause()
    }

    private fun setupData() {
        val proyectoId = activity!!.intent.getIntExtra("ProyectoId",0)
        video = identificarPr(proyectoId)
    }
    private fun identificarPr(proyectoId: Int): Proyectos {
        var hola: Proyectos? = null
        objetos.forEach{
            if(it.id.equals(proyectoId)) {
                hola = it
            }
        }
        return hola!!
    }

    private fun setupView() {
        val playerAdapter = MediaPlayerAdapter(activity)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        transportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        transportControlGlue.host = VideoSupportFragmentGlueHost(this)
        transportControlGlue.title = video.titulo
        transportControlGlue.subtitle = video.descripcion
        transportControlGlue.playWhenPrepared()

        playerAdapter.setDataSource(Uri.parse(video.VideoEntero))
    }

}