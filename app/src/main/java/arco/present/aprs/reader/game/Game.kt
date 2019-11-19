package arco.present.aprs.reader.game

import android.content.Context
import android.util.Log
import android.widget.Toast
import arco.present.aprs.reader.common.*
import arco.present.aprs.reader.decode.AprsDecoder
import arco.present.aprs.reader.decode.DecoderTask
import arco.present.aprs.reader.decode.PareserBeacon
import arco.present.aprs.reader.decode.ParserBeaconI
import arco.present.aprs.reader.record.RecordJThread
import arco.present.aprs.reader.record.RecordJava
import arco.present.aprs.reader.ui.ToolsFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class Game private constructor(ctx: ToolsFragment): GameI {


    companion object : SingletonHolder<Game, ToolsFragment>(::Game)

    init {
        Log.i(TAGLOG, "Create Game")
        gameCounter++
    }

    private var recordJava: RecordJava = RecordJava(ctx.activity);
    var recorderJava: RecordJava = RecordJava(ctx.activity)
    var recorderJThread: RecordJThread = RecordJThread(ctx, File(recordingFile))
    var context: Context = ctx.context!!;
    var frag = ctx;
    var stop = true;
    var parserBecon: ParserBeaconI = PareserBeacon(ctx);
    //var decodeTask = DecoderTask(parserBecon)

    fun startGame() {
        Log.i(TAGLOG, "Start game $gameCounter")

        if (stop) {
            stop = false;
            recordAndDecode()
        }
    }

    fun stopGame() {
        Log.i(TAGLOG, "Stop game $gameCounter")
        try {
            stop = true;

        } catch (e: Exception) {
            Log.e(TAGLOG, "Error to stop ${e.localizedMessage}")
        }
    }

    fun recordAndDecode() {
        if (stop) {
            Log.i(TAGLOG, "Stop game")
            return;
        }
        GlobalScope.launch {

            Log.i(TAGLOG, "run game ${++counter}")

            Thread {
                recorderJThread.start()
                startDecode()
//                var dest = copyFile()
//                if (!dest.isEmpty())
//                    startDecode(dest)

            }.start()

            //recordJava = RecordJava(context);
            frag.activity?.runOnUiThread {
                Toast.makeText(context, recordingFile, Toast.LENGTH_LONG).show()
            }
            //recordJava?
            // .launchTask(recordingFile);
            delay(60000)
            recorderJThread.stopRecord()

            //recorderJava.stop()

        }
    }

    fun startDecode() {

        var dest = copyFile()
        Thread {
            if (!dest.isEmpty())
                startDecode(dest)
        }.start()
        Log.i(TAGLOG, "start again Clear")

        recordAndDecode()

    }

    fun copyFile(): String {

        try {
            var file = File(recordingFile)
            var decodingFile: String = AprsDecoder.getInstance(parserBecon).getNextValidFile()
            var dest = File(decodingFile)
            file.copyTo(dest, true)
            Log.i(TAGLOG, "Success copy file")
            return decodingFile;
        } catch (e: Exception) {
            Log.e(TAGLOG, "Error copy file", e)
            return "";
        }
    }

    fun startDecode(dest: String) {

        try {
            Log.i(TAGLOG, "Decode game ${counter}")

            AprsDecoder.getInstance(parserBecon).decodeNextFile(dest, this)
        } catch (e: Exception) {
            Log.e(TAGLOG, "Error Start decode file", e)

        }

    }

    override fun clearGame(index: Int) {
        Log.i(TAGLOG, "game Clear game called")
        //AprsDecoder.getInstance(parserBecon).clearFile(index)
    }

}

interface GameI {

    fun clearGame(index: Int);

}
