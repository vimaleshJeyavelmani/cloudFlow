
class FlinkValidation extends FlinkStreamlet with LazyLogging {

 
  val inputOneIn = AvroInlet[InputOne]("input-one-in")
  val inputTwoIn = AvroInlet[InputTwo]("input-two-in")
  
  val shape = StreamletShape.withInlets(inputOneIn, inputTwoIn)

 
  override def createLogic() =
    new FlinkStreamletLogic {
      override def buildExecutionGraph = {

        val inputOneInStream: DataStream[InputOne] =
          readStream(inputOneIn).process(new ParseInputOne())

       
        val broadcastStateDescriptor =
          new MapStateDescriptor[String, InputOne](
            "input1",
            classOf[String],
            classOf[InputOne])

        val keyedInputTwo : KeyedStream[InputTwo, String] = readStream(inputTwoIn).keyBy(_.data.id)

        val broadcastInputOne: BroadcastStream[InputOne] =
          configurationStream.broadcast(broadcastStateDescriptor)


        val processStream: DataStream[Ouput] = keyedInputTwo
          .connect(broadcastInputOne)
          .process(new InputValidator())

        val outputStream: DataStream[Ouput] = AsyncDataStream
          .orderedWait(
            processStream,
            new AsynchEventMessageValidator,
            5,
            TimeUnit.SECONDS, // timeout requests after 5 seconds
            100
          ) // at most 100 concurrent requests

      }
    }
}
