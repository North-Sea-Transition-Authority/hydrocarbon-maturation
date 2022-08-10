<#import 'locationInput.ftl' as locationInput/>

<#macro coordinateInput
  degreesLocationPath
  minutesLocationPath
  secondsLocationPath
  formId
  directionList=[]
  direction="NS"
  directionPath=""
  labelText=""
  hintText="From 45 to 64 for degrees, 0 to 59 for minutes, and 0 to 59.999 for seconds"
  optionalLabel="fromInterceptor"
  nestingPath=""
  formGroupClass="">
    <@locationInput.locationInput
      degreesLocationPath=degreesLocationPath
      minutesLocationPath=minutesLocationPath
      secondsLocationPath=secondsLocationPath
      formId=formId
      directionList=directionList
      direction=direction
      directionPath=directionPath
      labelText=labelText
      hintText=hintText
      optionalLabel=optionalLabel
      nestingPath=nestingPath
      formGroupClass=formGroupClass/>
</#macro>