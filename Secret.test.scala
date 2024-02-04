package dev.carisey.secret

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SecretTest extends AnyFlatSpec with Matchers {

  behavior of "encode"

  it should "return 0 when encode message with itself as key" in {
    Secret.encode(Array(42.toByte))(Array(42.toByte)) shouldEqual Array(
      0.toByte
    )
    val key = "foo bar"
    Secret.encode(key.getBytes)(key.getBytes) shouldEqual Array.fill(7)(
      0.toByte
    )
  }

  it should "encode message with key size < message size" in {
    val key = "foo bar"
    val clearMessage = "something new to encode".getBytes
    val encodedMessage = Secret.encode(key.getBytes)(clearMessage)
    val decodedMessage = Secret.encode(key.getBytes)(encodedMessage)
    decodedMessage shouldEqual clearMessage
  }

  it should "encode message with key size > message size" in {
    val key = "foo bar as a big key"
    val clearMessage = "something".getBytes
    val encodedMessage = Secret.encode(key.getBytes)(clearMessage)
    val decodedMessage = Secret.encode(key.getBytes)(encodedMessage)
    decodedMessage shouldEqual clearMessage
  }
}
