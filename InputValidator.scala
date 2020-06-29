
class InputValidator
  extends KeyedBroadcastProcessFunction[String, InputTwo, InputOne, Ouput] with LazyLogging {

  private lazy val broadcastStateDescriptor =
    new MapStateDescriptor[String, InputOne]("input1", classOf[String], classOf[InputOne])

  
  lazy val output : OutputTag[String] = new OutputTag[String]("output")

  override def open(parameters: Configuration): Unit = {
   
  }

  override def processElement(
    value: InputTwo,
    readOnlyCtx: KeyedBroadcastProcessFunction[
      String,
      InputTwo,
      InputOne,
      Result]#ReadOnlyContext,
    out: Collector[Result]): Unit = {

    val stateValues = readOnlyCtx.getBroadcastState(broadcastStateDescriptor)

    println("readOnlyCtx --->>>" +  readOnlyCtx.getBroadcastState(broadcastStateDescriptor) )

	
	//while running test cases this stateValues is empty 

    stateValues.immutableEntries().forEach(a => {
      logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~")

      logger.info(" key ~~~~~>>> " + a.getKey)

      logger.info(" key ~~~~~>>> " + a.getValue)

      logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~")
    })


	// process logic
   

  }

  override def processBroadcastElement(
    value: InputOne,
    ctx: KeyedBroadcastProcessFunction[String, InputTwo, InputOne, Result]#Context,
    out: Collector[Result]): Unit = {

    val streamValidationConfig = ctx.getBroadcastState(broadcastStateDescriptor)

    streamValidationConfig.put(value.id, value)

  }
}
