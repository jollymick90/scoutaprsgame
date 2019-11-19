package arco.present.aprs.reader.ui

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import arco.present.aprs.reader.R
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.common.mapFragment
import arco.present.aprs.reader.common.outputFile
import arco.present.aprs.reader.common.recordingFile
import arco.present.aprs.reader.decode.*
import arco.present.aprs.reader.game.Game
import arco.present.aprs.reader.play.AprsPlayer
import arco.present.aprs.reader.record.RecordJava
import arco.present.aprs.reader.record.RecorderTask
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import java.io.File

class ToolsFragment : Fragment(), PrintInfo, AprsDecoderI{

    var map: MapView? = null;
    private var recorderTask: RecorderTask? = null
    private var recordJava: RecordJava? = null;
    val file = File(recordingFile)
    lateinit var textView: TextView;
    var parserBecon: ParserBeaconI = PareserBeacon(this);
    var decodeTask = DecoderTask(parserBecon, 0,this)
    companion object {
        fun newInstance(): ToolsFragment {

            return ToolsFragment();
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)

        map = view.findViewById<MapView>(arco.present.aprs.reader.R.id.map)
        map?.let {
            it.setTileSource(TileSourceFactory.MAPNIK)

            it.setMultiTouchControls(true)
            it.controller.setZoom(12.1)
            it.controller.setCenter(GeoPoint(45.4328338,12.1722371))
        }
        //marker()
        mapFragment = this;


        val decBtn = view.findViewById<Button>(R.id.decBtn)
        val recBtn = view.findViewById<Button>(R.id.recBtn)
        val stopRecBtn = view.findViewById<Button>(R.id.stopRecBtn)
        val playBtn = view.findViewById<Button>(R.id.playBtn)
        val stopBtn = view.findViewById<Button>(R.id.stopBtn)
        val startGameBtn = view.findViewById<Button>(R.id.startGameBtn)
        val stopGameBtn = view.findViewById<Button>(R.id.stopGameBtn)

        decBtn.setOnClickListener { view -> decode()}
        recBtn.setOnClickListener { view -> record()}
        playBtn.setOnClickListener { view -> play()}
        stopBtn.setOnClickListener { view -> stop()}
        stopRecBtn.setOnClickListener { view -> stopRecord()}
        startGameBtn.setOnClickListener { view -> startGame() }
        stopGameBtn.setOnClickListener { view -> stopGame() }

        textView = view.findViewById(R.id.textMsg)
        return view
    }

    private fun startGame() {
        //Start Record every time
        this?.let { Game.getInstance(it).startGame() };
    }

    private fun stopGame() {
        this?.let { Game.getInstance(it).stopGame() };
    }

    private fun stopRecord() {
        recordJava?.let {
            it.stop()
        }
    }

    private fun decode() {
        Log.i(TAGLOG, "DECODE")


        decodeTask.execute(file)
    }

    private fun record() {
        recordJava = RecordJava(activity);
        recordJava?.launchTask(recordingFile);
//        recorderTask = context?.let { RecorderTask(it, this) }
//        recorderTask?.let {
//            Log.i(TAGLOG, "RECORD")
//            it.execute(file)
//        }
    }

    private fun play() {
        Log.i(TAGLOG, "PLAY")
        AprsPlayer.newInstance().play()
    }

    private fun stop() {
        Log.i(TAGLOG, "STOP PLAY")
        AprsPlayer.newInstance().stopPlayer()
    }

   override fun printInfo(msg: String) {
       if (msg == null)
           return;
       activity?.runOnUiThread {
           textView?.let {
               it.setText(msg)
           }
       }

   }

    override fun clearGame(index: Int) {
        Log.i(TAGLOG, "CLEAR ${index} Tools fragment")
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun marker() {
        //your items
        val items = ArrayList<OverlayItem>()
        items.add(OverlayItem("Title", "Description", GeoPoint(45.4328338.toDouble(),12.1722371.toDouble()))) // Lat/Lon decimal degrees

//the overlay
        val mOverlay = ItemizedOverlayWithFocus<OverlayItem>(
            activity,
            items, //  <--------- added Context this as first parameter
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    //do something
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    return false
                }
            })  // <----- removed the mResourceProxy parameter
        mOverlay.setFocusItemsOnTap(true)

        map?.let { it.getOverlays().add(mOverlay) }
    }

    fun addMarker(lat:Double, lon: Double) {
        Log.i(TAGLOG, "ADDing MARKER")
        val items = ArrayList<OverlayItem>()
        items.add(OverlayItem("Title", "Description", GeoPoint(lat,lon))) // Lat/Lon decimal degrees

//the overlay
        activity?.let {
            val mOverlay = ItemizedOverlayWithFocus<OverlayItem>(
                it.applicationContext,
                items, //  <--------- added Context this as first parameter
                object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                    override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                        //do something
                        return true
                    }

                    override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                        return false
                    }
                })  // <----- removed the mResourceProxy parameter
            mOverlay.setFocusItemsOnTap(true)

            map?.let {

                Log.i(TAGLOG, "ADDing MARKER")

                it.getOverlays().add(mOverlay)
            }
        }

    }
}