//> using platform scala-native
//> using packaging.output secret
//> using scala 2.13
//> using test.dep org.scalatest::scalatest::3.2.17

package dev.carisey.secret

import java.io.File
import java.nio.file.Files
import java.util.Base64

object Secret {
  def encode(key: Array[Byte])(message: Array[Byte]): Array[Byte] = {
    assert(key != null && key.length > 0 && key.length < Int.MaxValue)
    assert(
      message != null && message.length > 0 && message.length < Int.MaxValue
    )

    val keySize: Int = key.length
    val messageSize: Int = message.length

    val result: Array[Byte] = new Array[Byte](messageSize)
    var indexStart = 0
    var indexKey = 0

    while (indexStart < messageSize) {
      if (indexKey >= keySize) indexKey = 0
      result(indexStart) = (key(indexKey) ^ message(indexStart)).toByte
      indexKey += 1
      indexStart += 1
    }

    result
  }

  final case class Args(decode: Boolean, keyFile: String, messageFile: String)

  object Args {
    def parse(args: Array[String]): Args = {
      val decode = args(0) == "-d"
      assert(
        !decode && args.length == 2 || decode && args.length == 3,
        "Usage : secret [-d] keyFile messageFile"
      )

      if (decode) Args(decode, args(1), args(2))
      else Args(false, args(0), args(1))
    }
  }

  def main(params: Array[String]): Unit = {

    val args = Args.parse(params)

    val keyFile = new File(args.keyFile)
    assert(keyFile.canRead, s"Cannot read key file ${args.keyFile}")
    val messageFile = new File(args.messageFile)
    assert(messageFile.canRead, s"Cannot read messageFile ${args.messageFile}")

    val key = Files.readAllBytes(keyFile.toPath)
    val message =
      if (args.decode)
        Base64.getMimeDecoder.decode(
          new String(Files.readAllBytes(messageFile.toPath), "UTF-8")
        )
      else
        Files.readAllBytes(messageFile.toPath)

    val encodedMessage =
      if (args.decode)
        Secret.encode(key)(message)
      else
        Base64.getMimeEncoder.encode(Secret.encode(key)(message))

    println(new String(encodedMessage))

    System.exit(0)
  }
}
