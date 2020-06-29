
class FlinkValidationTest extends FlinkTestkit with AnyWordSpecLike with Matchers with BeforeAndAfterAll with LazyLogging {
  "process Validation" in {
    @transient lazy val env = StreamExecutionEnvironment.getExecutionEnvironment

    val flinkValidationStreamlet = new FlinkValidation

    val inputOne : InputOne = TestMock.getInputone()
    val inputTwo : InputTwo = TestMock.getInputTwo()


    val inputOneIn: FlinkInletTap[InputOne] = inletAsTap[InputOne](
      flinkValidationStreamlet.inputOne,
      env.addSource(FlinkSource.CollectionSourceFunction(Seq(inputOne))))

    val inputTwoIn: FlinkInletTap[InputTwo] = inletAsTap[InputTwo](
      flinkValidationStreamlet.inputTwo,
      env.addSource(FlinkSource.CollectionSourceFunction(Seq(inputTwo))))

    run(flinkValidationStreamlet, Seq(inputOneIn,inputTwoIn), Seq(), env)

    println(TestFlinkStreamletContext.result.size)

  }
}