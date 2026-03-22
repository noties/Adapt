
## Screenshot tests

Records all the tests as golden values...
```
./gradlew :sample:recordPaparazziDebug
```

Verifies all the tests against golden values...
```
./gradlew :sample:verifyPaparazziDebug
```

<!-- Not sure how to record only new, that do not have golden value yet... not very helpful it seems -->