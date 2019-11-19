package arco.present.aprs.reader.decode

import android.util.Log
import arco.present.aprs.reader.common.SingletonHolder
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.common.decodingBaseFile
import arco.present.aprs.reader.common.ext
import arco.present.aprs.reader.game.GameI
import java.io.File

class AprsDecoder private constructor(parserBeacon: ParserBeaconI): AprsDecoderI {

    companion object : SingletonHolder<AprsDecoder, ParserBeaconI>(::AprsDecoder)
    var allTask: MutableMap<Int, DecoderTask> = HashMap<Int, DecoderTask>();
    var pathTask: MutableMap<Int, String> = HashMap<Int, String>();
    var index: Int = 0;

    var parserBeacon = parserBeacon
    var gameI: GameI? = null;
    fun getNextValidFile(): String {


        try {
            var path = decodingBaseFile + index + ext
            Log.i(TAGLOG, "GET #$index index. Path:" + path)
            pathTask.put(index, path)
            return path
        } catch (e: Exception) {
            Log.e(TAGLOG, "Error get next valid file ", e)
            return ""
        }
    }

    fun decodeNextFile(path: String, gameI: GameI): Int {

        try {
            this.gameI = gameI;
            //var decodeTask = DecoderTask(parserBeacon, index,this)
            var file = File(path)
            //decodeTask.execute(file)
            //allTask.put(index, decodeTask)

            var decodeThread =  DecoderThread(parserBeacon, index,this, file)
            val t = Thread(decodeThread)
            t.start()
            Log.i(TAGLOG, "start decode #$index index. Path:" + path)

            return index++
        } catch (e: Exception) {
            Log.e(TAGLOG, "Error decode next valid file ", e)

            return -1;
        }
    }

    override fun clearGame(index: Int) {
        clearFile(index)
        if (gameI != null)
            gameI!!.clearGame(index);

    }

    fun clearFile(index: Int): Boolean {
//        try {
//
//            var task = allTask.get(index)
//            task?.cancel(true)
//
//        } catch (e: Exception) {
//            Log.e(TAGLOG, "Error clear $index task ", e)
//
//        }

        try {

            var path = pathTask.get(index)
            var file = File(path)
            if (file.delete()) {
                Log.i(TAGLOG, "Success delete $path ")
                return true

            } else {
                Log.e(TAGLOG, "Error delete $index path $path ")
                return false

            }
        } catch (e: Exception) {
            Log.e(TAGLOG, "Error delete $index path ", e)

            return false;
        }
    }


}