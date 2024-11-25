#!/bin/sh

# execute test and checks
./gradlew \
  :adapt:test \
  :adapt-kt:test \
  :adapt-ui:test \
  :adapt-ui-flex:test \
  :sample:test \
  :sample:verifyPaparazziDebug --continue