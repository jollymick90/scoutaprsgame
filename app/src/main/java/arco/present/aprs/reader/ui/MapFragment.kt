package arco.present.aprs.reader.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.common.mapFragment
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus

class MapFragment : Fragment() {


    companion object {
        fun instance(): MapFragment {
            return MapFragment();
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(arco.present.aprs.reader.R.layout.fragment_map, container, false)
//        map = view.findViewById<MapView>(arco.present.aprs.reader.R.id.map)
//        map?.let {
//            it.setTileSource(TileSourceFactory.MAPNIK)
//
//            it.setMultiTouchControls(true)
//            it.controller.setZoom(12.1)
//            it.controller.setCenter(GeoPoint(45.4328338,12.1722371))
//        }
//        //marker()
//        mapFragment = this;
        return view
    }

//    fun marker() {
//        //your items
//        val items = ArrayList<OverlayItem>()
//        items.add(OverlayItem("Title", "Description", GeoPoint(45.4328338.toDouble(),12.1722371.toDouble()))) // Lat/Lon decimal degrees
//
////the overlay
//        val mOverlay = ItemizedOverlayWithFocus<OverlayItem>(
//            activity,
//            items, //  <--------- added Context this as first parameter
//            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
//                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
//                    //do something
//                    return true
//                }
//
//                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
//                    return false
//                }
//            })  // <----- removed the mResourceProxy parameter
//        mOverlay.setFocusItemsOnTap(true)
//
//        map?.let { it.getOverlays().add(mOverlay) }
//    }
//
//    fun addMarker(lat:Double, lon: Double) {
//        Log.i(TAGLOG, "ADDing MARKER")
//        val items = ArrayList<OverlayItem>()
//        items.add(OverlayItem("Title", "Description", GeoPoint(lat,lon))) // Lat/Lon decimal degrees
//
////the overlay
//        activity?.let {
//            val mOverlay = ItemizedOverlayWithFocus<OverlayItem>(
//                it.applicationContext,
//                items, //  <--------- added Context this as first parameter
//                object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
//                    override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
//                        //do something
//                        return true
//                    }
//
//                    override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
//                        return false
//                    }
//                })  // <----- removed the mResourceProxy parameter
//            mOverlay.setFocusItemsOnTap(true)
//
//            map?.let {
//
//                Log.i(TAGLOG, "ADDing MARKER")
//
//                it.getOverlays().add(mOverlay)
//            }
//        }
//
//    }

}