package sangria.marshalling

import org.msgpack.value.ValueFactory

import sangria.marshalling.testkit._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MsgpackSupportSpec extends AnyWordSpec with Matchers with MarshallingBehaviour with InputHandlingBehaviour with ParsingBehaviour {
  import sangria.marshalling.msgpack._

  "Msgpack integration" should {
    behave like `value (un)marshaller` (msgpackResultMarshaller)

    behave like `AST-based input unmarshaller` (msgpackFromInput)
    behave like `AST-based input marshaller` (msgpackResultMarshaller)

    behave like `input parser` (ParseTestSubjects(
      complex = "gqFhk8B7kYGjZm9vo2JhcqFigqFjw6FkwA==",
      simpleString = "o2Jhcg==",
      simpleInt = "zTA5",
      simpleNull = "wA==",
      list = "laNiYXIBwMOTAQID",
      syntaxError = List("jhdskjfdskjfkhdsg")
    ))
  }

  val toRender = ValueFactory.newMap(
    ValueFactory.newString("a"), ValueFactory.newArray(
      ValueFactory.newNil(), ValueFactory.newInteger(123), ValueFactory.newArray(
        ValueFactory.newMap(ValueFactory.newString("foo"), ValueFactory.newString("bar")))),
    ValueFactory.newString("b"), ValueFactory.newMap(
      ValueFactory.newString("c"), ValueFactory.newBoolean(true),
      ValueFactory.newString("d"), ValueFactory.newNil()))

  "InputUnmarshaller" should {
    "throw an exception on invalid scalar values" in {
      an [IllegalStateException] should be thrownBy
          msgpackInputUnmarshaller.getScalarValue(ValueFactory.emptyMap())
    }

    "throw an exception on variable names" in {
      an [IllegalArgumentException] should be thrownBy
          msgpackInputUnmarshaller.getVariableName(ValueFactory.newString("$foo"))
    }

    "render JSON values" in {
      val rendered = msgpackInputUnmarshaller.render(toRender)

      toRender.toJson should be("""{"a":[null,123,[{"foo":"bar"}]],"b":{"c":true,"d":null}}""")
      rendered should be ("gqFhk8B7kYGjZm9vo2JhcqFigqFjw6FkwA==")
    }
  }

  "ResultMarshaller" should {
    "render JSON values" in {
      val renderedCompact = msgpackResultMarshaller.renderCompact(toRender)
      val renderedPretty = msgpackResultMarshaller.renderCompact(toRender)

      renderedCompact should be (renderedPretty)

      renderedCompact should be ("gqFhk8B7kYGjZm9vo2JhcqFigqFjw6FkwA==")
    }

    val bytes = "foo bar".getBytes("UTF-8")

    import sangria.marshalling.msgpack.standardTypeBigDecimal._

    val rm = msgpackResultMarshaller
    val iu = msgpackInputUnmarshaller

    "thow an exception if number is too big" in {
      an [IllegalArgumentException] should be thrownBy
        rm.scalarNode(BigDecimal("1232343234234234432432.2435454354543"), "Test", Set.empty)
    }

    "marshal `Long` scalar values" in {
      val marshaled = rm.scalarNode(123434252243534L, "Test", Set.empty)

      marshaled should be (ValueFactory.newInteger(123434252243534L))
    }

    "(un)marshal big int scalar values" in {
      val marshaled = rm.scalarNode(BigDecimal("123346328764783268476238764"), "Test", Set.empty)

      val scalar = iu.getScalarValue(marshaled)
      val scalaScalar = iu.getScalaScalarValue(marshaled)

      iu.getScalaScalarValue(marshaled) should be (BigInt("123346328764783268476238764"))

      if (scalar != scalaScalar)
        scalar should be (marshaled)

      iu.isScalarNode(marshaled) should be (true)
      iu.isDefined(marshaled) should be (true)

      iu.isEnumNode(marshaled) should be (false)
      iu.isVariableNode(marshaled) should be (false)
      iu.isMapNode(marshaled) should be (false)
      iu.isListNode(marshaled) should be (false)
    }

    "marshal blob scalar values" in {
      val marshaled = rm.scalarNode(bytes, "Test", Set.empty)

      marshaled should be (ValueFactory.newBinary(bytes))
    }
  }
}
