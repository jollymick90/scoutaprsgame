package arco.present.aprs.reader.decode

interface ParserBeaconI {

    fun parseBeacon(msg: String);
}