package gittertwitterbot

import com.typesafe.scalalogging.StrictLogging

final class Gitter(gitterRoomId: String, gitterToken: String) extends StrictLogging {

  def postMessage(text: String): Unit = {
    import scalaj.http.Http
    import scalaj.http.HttpOptions
    import org.json4s._
    import org.json4s.native.JsonMethods._
    implicit val formats = DefaultFormats

    logger.info(text)

    val data = Extraction.decompose(Map("text" -> text))

    val (responseCode, headersMap, resultString) =
      Http
        .postData(s"https://api.gitter.im/v1/rooms/${gitterRoomId}/chatMessages", compact(render(data)).getBytes)
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .header("Authorization", s"Bearer $gitterToken")
        .option(HttpOptions.connTimeout(10 * 1000))
        .asHeadersAndParse(Http.readString)

    if (responseCode != 200) {
      logger.error(resultString)
    }
  }

}
