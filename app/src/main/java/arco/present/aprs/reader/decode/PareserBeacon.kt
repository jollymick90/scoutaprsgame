package arco.present.aprs.reader.decode

import android.util.Log
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.common.mainAct
import arco.present.aprs.reader.common.mapFragment
import arco.present.aprs.reader.ui.PrintInfo
import java.text.SimpleDateFormat
import java.util.*

class PareserBeacon(private val printInfo: PrintInfo) : ParserBeaconI {
    override fun parseBeacon(msg: String): Unit =
//[IK3CYN>APRS,WIDE2-1:/054159h4525.99N/01211.60E-000/000/A=000000/Ti=35/V=7415 Trackuino reminder: replace callsign with your own]
        try {
            if (msg.contains("[IK3CYN>APRS")) {
                var aprs = msg.indexOf("APRS,")
                var skip1 = msg.indexOf(":/", aprs)
                var firstI = msg.indexOf('h', skip1)
                var second = msg.indexOf('N', firstI)
                var coordN = msg.substring(firstI + 1, second)
                var firstEI = msg.indexOf("/", second)
                var secondEI = msg.indexOf("E", second)
                var coordE = msg.substring(firstEI + 1, secondEI)
                Log.i(TAGLOG, "coordN $coordN")
                Log.i(TAGLOG, "coordN $coordE")
                val sdf = SimpleDateFormat("hh:mm:ss")
                val currentDate = sdf.format(Date())

                val gradNStr = coordN.substring(0,2);
                val gradEStr = coordE.substring(0,3);
                val secNStr = coordN.substring(2)
                val secEStr = coordE.substring(3)


                try {
                    val gradN = gradNStr.toDouble()
                    val gradE = gradEStr.toDouble()
                    val secN = secNStr.toDouble()
                    val secE = secEStr.toDouble()
                    val sN = secN / 60;
                    val sE = secE / 60;
                    val lat = gradN + sN;
                    val lon = gradE + sE;

                    Log.i(TAGLOG, "lat $lat")
                    Log.i(TAGLOG, "lon $lon")
                    printInfo.printInfo("$currentDate Name: IK3CYN, COORDINATE: lat=$lat, lon=$lon")

                    //val gradEStr = coord
                    mainAct?.let {
                        it.runOnUiThread{
                            mapFragment?.let {
                                Log.i(TAGLOG, "put marker")
                                it.addMarker(lat,lon)
                            }
                        }
                    }!!
                } catch (e: Exception) {
                    Log.e(TAGLOG, "Error", e)
                    printInfo.printInfo("$currentDate Name: IK3CYN, COORDINATE: N:$coordN, E:$coordE")
                }

            } else {
                Log.i(TAGLOG, "Get Beacon from another Radio")
                printInfo.printInfo("Get Beacon from another Radio $msg")
            }

        } catch(e: Exception) {
            Log.e(TAGLOG, "Error to parse beacon: ", e)
            printInfo.printInfo("Errore $msg")
        }
}